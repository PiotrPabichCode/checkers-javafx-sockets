package com.example.checkersjavafxsockets.Game;

public enum MoveType {
    NONE, NORMAL, KILL, WIN;

    public String toString(){
        switch(this){
            case NONE:
                return "NONE";
            case NORMAL:
                return "NORMAL";
            case KILL:
                return "KILL";
            case WIN:
                return "WIN";
        }
        return "NONE";
    }
}
