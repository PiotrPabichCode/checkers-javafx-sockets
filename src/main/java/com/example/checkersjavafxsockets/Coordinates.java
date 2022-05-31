package com.example.checkersjavafxsockets;

public class Coordinates {
    private int x,y;

    public Coordinates(){}
    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void set(Coordinates coord){
        this.x = coord.x;
        this.y = coord.y;
    }
    public int subtractX(int value){
        return this.x - value;
    }
    public int subtractY(int value){
        return this.y - value;
    }
    public void multiply(double factor){
        this.x *= factor;
        this.y *= factor;
    }
}
