package com.example.checkersjavafxsockets.UI;

import com.example.checkersjavafxsockets.Coordinates;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;

public class Tile extends Rectangle {

    private Piece piece;

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public Piece getPiece(){
        return piece;
    }

    public boolean hasPiece(){
        return piece != null;
    }

    public Tile(boolean light, int x, int y){
        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);

        relocate(x * TILE_SIZE, y * TILE_SIZE);

        setFill(light ? Color.WHITE : Color.BLACK);
    }
}
