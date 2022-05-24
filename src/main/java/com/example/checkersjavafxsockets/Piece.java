package com.example.checkersjavafxsockets;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;

public class Piece extends StackPane {

    private PieceType type;

    public PieceType getType() {
        return type;
    }

    public Piece(PieceType type, int x, int y){
        this.type = type;

        relocate(x * TILE_SIZE, y * TILE_SIZE);

        Ellipse background = createPieceBackgroundEllipse();
        Ellipse ellipse = createPieceMainEllipse();

        getChildren().addAll(background, ellipse);
    }

    private Ellipse createPieceMainEllipse(){
        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.RED ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        return ellipse;
    }

    private Ellipse createPieceBackgroundEllipse(){
        Ellipse background = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        background.setFill(type == PieceType.RED ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));

        background.setStroke(Color.BLACK);
        background.setStrokeWidth(TILE_SIZE * 0.03);

        background.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        background.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);
        return background;
    }

}
