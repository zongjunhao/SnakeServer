package com.snake.common.bean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 用点组成贪吃蛇的身体，有x,y两个位置属性
 */
public class Point implements Serializable {
    private static final long serialVersionUID = 5886436511356524393L;
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(){

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        x = in.readFloat();
        y = in.readFloat();
    }
}
