package com.example.checkersjavafxsockets.Game;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.StackPane;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;

public class Timer extends StackPane{
    private final SimpleStringProperty time = new SimpleStringProperty("Playtime: 0s.");
    public static final double TIMER_WIDTH = TILE_SIZE;
    public static final double TIMER_HEIGHT = TILE_SIZE;

    public Timer(){
        Rectangle timerRectangle = new Rectangle();
        timerRectangle.setFill(Color.GREEN);
        timerRectangle.setWidth(TIMER_WIDTH);
        timerRectangle.setHeight(TIMER_HEIGHT);
        timerRectangle.setStroke(Color.BLACK);

        Label timerLabel = new Label();
        timerLabel.setTextFill(Color.BLACK);
        timerLabel.textProperty().bind(time);
        timerLabel.setTextAlignment(TextAlignment.CENTER);
        timerLabel.setFont(new Font(15));

        getChildren().addAll(timerRectangle, timerLabel);

        relocate(0.0f, 0.0f);



    }
    public void setTime(String string){
        time.set(string);
    }
}
