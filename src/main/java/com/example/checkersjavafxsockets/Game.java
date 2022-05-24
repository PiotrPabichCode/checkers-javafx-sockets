package com.example.checkersjavafxsockets;

import javafx.scene.Group;

public class Game {
    private Group whiteGroup;
    private Group redGroup;

    Game(Group whiteGroup, Group redGroup){
        this.whiteGroup = whiteGroup;
        this.redGroup = redGroup;
    }

    public Group getWhiteGroup() {
        return this.whiteGroup;
    }

    public Group getRedGroup() {
        return this.redGroup;
    }
}
