package com.example.checkersjavafxsockets.Game;

public enum MoveType {
    NONE, NORMAL, KILL, WHITENOW, REDNOW, WIN;

    public String toString(){
        switch(this){
            case NONE:
                return "NONE";
            case NORMAL:
                return "NORMAL";
            case KILL:
                return "KILL";
            case WHITENOW:
                return "WHITENOW";
            case REDNOW:
                return "REDNOW";
            case WIN:
                return "WIN";
        }
        return "NONE";
    }
    public static MoveType getType(String type){
        switch (type){
            case "NONE":
                return NONE;
            case "NORMAL":
                return NORMAL;
            case "KILL":
                return KILL;
            case "WHITENOW":
                return WHITENOW;
            case "REDNOW":
                return REDNOW;
            case "WIN":
                return WIN;
        }
        return null;
    }
}
