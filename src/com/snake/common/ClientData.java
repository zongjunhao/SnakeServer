package com.snake.common;

import com.snake.common.bean.Point;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ClientData implements Serializable {
    private static final long serialVersionUID = 7246863764324943042L;
    private int flag;
    private Point opt;
    private int speed;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Point getOpt() {
        return opt;
    }

    public void setOpt(Point opt) {
        this.opt = opt;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.write(flag);
        out.writeObject(opt);
        out.write(speed);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
       flag = in.read();
       opt = (Point)in.readObject();
       speed = in.read();
    }
}
