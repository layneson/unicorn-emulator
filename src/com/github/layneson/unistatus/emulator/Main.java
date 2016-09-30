package com.github.layneson.unistatus.emulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;



public class Main extends Application {

    public static final int WIDTH = 500, HEIGHT = 500;

    public static Color[] buffer = new Color[8 * 8];

    public static double brightness = 1;

    public static GraphicsContext context;

    private static Server server;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane parent = new BorderPane();

        Canvas c = new Canvas(WIDTH, HEIGHT);
        context = c.getGraphicsContext2D();

        parent.setCenter(c);

        Scene sc = new Scene(parent, WIDTH, HEIGHT);
        stage.setScene(sc);

        clearBuffer();
        updateBuffer();

        stage.setTitle("Unicorn HAT Emulator");
        stage.show();

        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    server = new Server(22551);
                    server.start();
                } catch (Exception e) {
                }
            }
        };
        serverThread.start();
    }

    @Override
    public void stop() {
        server.stop();
    }

    public static void clearBuffer() {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = Color.rgb(0, 0, 0);
        }
    }

    private static double padding = 10;
    public static void updateBuffer() {
        context.clearRect(0, 0, WIDTH, HEIGHT);

        context.setFill(javafx.scene.paint.Color.BLACK);
        context.fillRect(0, 0, WIDTH, HEIGHT);

        context.setStroke(javafx.scene.paint.Color.WHITE);

        double circleWidth = (WIDTH-padding*9)/8.0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                context.setFill(buffer[x * 8 + y].deriveColor(0, 1, brightness, 1));
                context.fillOval(padding + x*(circleWidth+padding), padding + y*(circleWidth+padding), circleWidth, circleWidth);
                context.strokeOval(padding + x*(circleWidth+padding), padding + y*(circleWidth+padding), circleWidth, circleWidth);
            }
        }
    }


    public static void setPixel(int x, int y, int r, int g, int b) {
        buffer[x * 8 + y] = Color.rgb(r, g, b);
    }


    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
