package com.example.checkersjavafxsockets.Game;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.StackPane;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;
import static com.example.checkersjavafxsockets.Checkers.MAX_SIZE;

public class Timer extends StackPane{
    public static final double TIMER_WIDTH = TILE_SIZE * 1.6f;
    public static final double TIMER_HEIGHT = TILE_SIZE * 0.4f;
    private final StringProperty time = new SimpleStringProperty("Timer: 0s.");

    public Timer(){
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.GREEN);
        rectangle.setArcHeight(20.0f);
        rectangle.setArcWidth(20.0f);
        rectangle.setWidth(TIMER_WIDTH);
        rectangle.setHeight(TIMER_HEIGHT);
        rectangle.setStroke(Color.BLACK);

        Label label = new Label();
        label.setTextFill(Color.BLACK);
        label.textProperty().bind(time);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(new Font(TIMER_HEIGHT / 2.0f));

        getChildren().addAll(rectangle, label);

        relocate((TILE_SIZE * MAX_SIZE - TIMER_WIDTH) / 2.0f, 0.0f);

    }
    public void set(String string){
        time.set(string);
    }
}
