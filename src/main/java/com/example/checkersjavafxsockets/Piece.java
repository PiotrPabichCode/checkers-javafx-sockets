package com.example.checkersjavafxsockets;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;

public class Piece extends StackPane {

    private PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;

    public Piece(PieceType type, int x, int y){
        this.type = type;

        move(x, y);

        Ellipse background = createPieceBackgroundEllipse();
        Ellipse ellipse = createPieceMainEllipse();

        getChildren().addAll(background, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y){
        this.oldX = x * TILE_SIZE;
        this.oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove(){
        relocate(oldX, oldY);
    }

    private Ellipse createPieceMainEllipse(){
        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        return ellipse;
    }

    private Ellipse createPieceBackgroundEllipse(){
        Ellipse background = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        background.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);

        background.setStroke(Color.BLACK);
        background.setStrokeWidth(TILE_SIZE * 0.03);

        background.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        background.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);
        return background;
    }

    public PieceType getType() {
        return this.type;
    }
    public double getMouseX(){
        return this.mouseX;
    }
    public double getMouseY(){
        return this.mouseY;
    }
    public double getOldX(){
        return this.oldX;
    }
    public double getOldY(){
        return this.oldY;
    }

}
