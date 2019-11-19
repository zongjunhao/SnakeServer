package com.snake.common.bean;

import java.io.*;
import java.util.List;
import java.util.Vector;

public class SnakeBean implements Serializable {
    private static final long serialVersionUID = 5368708600381645862L;
    public Point head;//头部点
    public List<Point> snake;//用列表保存蛇的身体
    public int snakeColor;//蛇的颜色
    public boolean isDeath = false;//标志贪吃蛇是否死亡
    public int eat;//分数
    public int flag;//用于区别每条贪吃蛇
    public String name;//贪吃蛇名字

    public SnakeBean(){

    }
    public void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(head);
        out.writeObject(snake);
        out.write(snakeColor);
        out.writeBoolean(isDeath);
        out.write(eat);
        out.write(flag);
        out.writeUTF(name);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            head = (Point)in.readObject();
            snake = (List<Point>)in.readObject();
            snakeColor = in.read();
            isDeath = in.readBoolean();
            eat = in.read();
            flag = in.read();
            name = in.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }
}
