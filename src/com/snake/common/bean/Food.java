package com.snake.common.bean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 食物，有x,y位置属性和颜色属性
 */
public class Food implements Serializable {
    private static final long serialVersionUID = 5673634403814493094L;
    private int x;
    private int y;
    private int color;
    private boolean isNull;

    public Food(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        isNull = false;
    }

    public Food(){

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.write(x);
        out.write(y);
        out.write(color);
        out.writeBoolean(isNull);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        x = in.read();
        y = in.read();
        color = in.read();
        isNull = in.readBoolean();
    }
}
