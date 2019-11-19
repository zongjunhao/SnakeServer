package com.snake.common;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private int age;
    private long time;

    public User(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.time = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
