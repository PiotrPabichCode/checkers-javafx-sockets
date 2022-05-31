package com.example.checkersjavafxsockets.UI;

import com.example.checkersjavafxsockets.Coordinates;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.example.checkersjavafxsockets.Checkers.TILE_SIZE;

public class Piece extends StackPane {

    private PieceType type;
    private double mouseX, mouseY;
    private final Coordinates oldCoord = new Coordinates();

    public Piece(PieceType type, int x, int y){
        this.type = type;
        this.oldCoord.set(x * TILE_SIZE, y * TILE_SIZE);

        relocate(oldCoord.getX(), oldCoord.getY());

        Ellipse background = createPieceBackgroundEllipse();
        Ellipse ellipse = createPieceMainEllipse();

        getChildren().addAll(background, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            double xValue = e.getSceneX() - mouseX + oldCoord.getX();
            double yValue = e.getSceneY() - mouseY + oldCoord.getY();
            if(validPosition(xValue, yValue)){
                relocate(xValue, yValue);
            }
        });
    }

    private boolean validPosition(double xValue, double yValue){
        double LEFT_BORDER = -40;
        double RIGHT_BORDER = 740;
        double UPPER_BORDER = 740;
        double DOWN_BORDER = -40;
        if(xValue > LEFT_BORDER && xValue < RIGHT_BORDER && yValue > DOWN_BORDER && yValue < UPPER_BORDER){
            return true;
        }
        return false;
    }

    public void move(Coordinates coord){
        oldCoord.set(coord);
        oldCoord.multiply(TILE_SIZE);
        relocate(oldCoord.getX(), oldCoord.getY());
    }

    public void abortMove(){
        relocate(oldCoord.getX(), oldCoord.getY());
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
    public void promotePiece(){
        Ellipse promotedPiece = new Ellipse(TILE_SIZE * 0.3125 / 2, TILE_SIZE * 0.26 / 2);
        promotedPiece.setFill(type == PieceType.RED ? Color.RED : Color.WHITE);
        promotedPiece.setStroke(Color.BLACK);
        promotedPiece.setStrokeWidth(TILE_SIZE * 0.03);
        promotedPiece.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        promotedPiece.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        getChildren().addAll(promotedPiece);

        type = (type == PieceType.RED) ? PieceType.RED_PROMOTED : PieceType.WHITE_PROMOTED;
    }

    public PieceType getType() {
        return this.type;
    }
    public double getOldX(){
        return oldCoord.getX();
    }
    public double getOldY(){
        return oldCoord.getY();
    }
    public Coordinates getOldCoord(){
        return this.oldCoord;
    }

}
