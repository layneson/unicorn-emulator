package com.github.layneson.unistatus.emulator;

import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Server {

    public static final int PACKET_SIZE = 50;

    private final int port;
    private DatagramSocket socket;

    public Server(int port) {
        this.port = port;
    }

    public void stop() {
        socket.close();
    }

    public void start() throws IOException {
        socket = new DatagramSocket(port);

        byte[] buff = new byte[PACKET_SIZE];
        while (true) {
            DatagramPacket dp = new DatagramPacket(buff, buff.length);

            socket.receive(dp);

            switch (buff[0]) {
                case 0:
                    packetInit(dp);
                    break;
                case 1:
                    packetBrightness(dp);
                    break;
                case 2:
                    packetSetPixel(dp);
                    break;
                case 3:
                    packetShow(dp);
                    break;
                default:
                    System.out.println("Unrecognized packet!");
            }
        }
    }

    private void packetInit(DatagramPacket packet) throws IOException {
        Platform.runLater(() -> {
            Main.clearBuffer();
            Main.updateBuffer();
        });

        byte[] buff = new byte[PACKET_SIZE];
        buff[0] = 4;

        DatagramPacket out = new DatagramPacket(buff, buff.length, packet.getAddress(), packet.getPort());
        socket.send(out);
    }

    private void packetBrightness(DatagramPacket packet) throws IOException {
        ByteBuffer buff = ByteBuffer.wrap(packet.getData());
        buff.order(ByteOrder.BIG_ENDIAN);

        buff.get();

        int brightness = buff.getInt();
        Main.brightness = brightness/255.0;
    }

    private void packetSetPixel(DatagramPacket packet) throws IOException {
        ByteBuffer buff = ByteBuffer.wrap(packet.getData());
        buff.order(ByteOrder.BIG_ENDIAN);

        buff.get();

        int x = buff.getInt();
        int y = buff.getInt();
        int r = buff.getInt();
        int g = buff.getInt();
        int b = buff.getInt();

        Platform.runLater(() -> Main.setPixel(x, y, r, g, b));
    }

    private void packetShow(DatagramPacket packet) throws IOException {
        Platform.runLater(() -> Main.updateBuffer());
    }
}
