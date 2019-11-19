package com.snake.server.view;

import com.snake.common.ClientData;
import com.snake.common.bean.Food;
import com.snake.common.bean.SnakeBean;
import com.snake.server.bean.Snake;

import java.util.List;
import java.util.Vector;

public interface SnakeViewInterface {

    /**
     * 获取所有的蛇，用于发送至客户端
     * @return
     */
    List<SnakeBean> getSnakes();
    /**
     * 获取所有的食物，用于发送至客户端
     * @return
     */
    List<Food> getFoods();

    /**
     * 设置蛇
     * @param flag 蛇的标记
     * @param k 蛇被吃的位置
     */
    void removeSnake(int flag, int k);
    /**
     * 设置食物
     * @param k 食物的位置
     */
    void removeFood(int k);

    /**
     * 获取所有的蛇
     * @return
     */
    List<Snake> getSnakesInView();

    /**
     * 处理从客户端接收的数据
     * @param clientData 客户端接收的数据
     */
    void setData(ClientData clientData);

    List<Food> getFoodsData();
    List<SnakeBean> getSnakeBeans();

    void addSnake(int flag, boolean isAI, String name);
}

