/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.commands.utils;

import codinggame.lang.Parser;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public enum Direction {
    UP(0, 1), RIGHT(1, 0), DOWN(0, -1), LEFT(-1, 0);

    public int x, y;

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX(int robotX) {
        return x + robotX;
    }

    public int getY(int robotY) {
        return y + robotY;
    }

    public static Direction get(Parser parser, String name) {
        Direction[] directions = values();
        for (Direction direction : directions) {
            if (direction.name().equalsIgnoreCase(name)) {
                return direction;
            }
        }
        parser.throwParsingError(name + " is not a valid go direction");
        return null;
    }
    
    public Vector2i moveVector(int magnitude) {
        return new Vector2i(x, y).mul(magnitude);
    }
}
