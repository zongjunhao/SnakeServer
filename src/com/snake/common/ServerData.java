package com.snake.common;

import com.snake.common.bean.Food;
import com.snake.common.bean.SnakeBean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class ServerData implements Serializable {
    private static final long serialVersionUID = 7127781207551345934L;
    private List<SnakeBean> snakes;
    private List<Food> foods;
    private String rank;
    private int flag;

    public ServerData(List<SnakeBean> snakes, List<Food> foods, String rank, int flag){
        this.snakes = snakes;
        this.foods = foods;
        this.rank = rank;
        this.flag = flag;
    }

    public ServerData(){

    }

    public List<SnakeBean> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<SnakeBean> snakes) {
        this.snakes = snakes;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }



    public void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(snakes);
        out.writeObject(foods);
        out.writeUTF(rank);
        out.write(flag);
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            snakes = (List<SnakeBean>) in.readObject();
            foods = (List<Food>) in.readObject();
            rank = in.readUTF();
            flag = in.read();
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }

}
