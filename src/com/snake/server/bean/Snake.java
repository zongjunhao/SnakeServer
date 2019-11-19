package com.snake.server.bean;

import com.snake.common.Constant;
import com.snake.common.bean.Food;
import com.snake.common.bean.Point;
import com.snake.common.bean.SnakeBean;
import com.snake.server.base.Color;
import com.snake.server.view.SnakeViewInterface;

import java.util.*;

public class Snake extends SnakeBean {
    private Point opt;//贪吃蛇移动方向
    private SnakeViewInterface snakeView;
    private boolean isAI = false;//标志贪吃蛇是否为电脑AI

    private List<Snake> snakes;
    private List<Food> foods;
    private int isEat = 0;//贪吃蛇吃到的分数（还没有转化为身体的）
    private int speed = 1;//速度
    private int length;//长度
    private int[] colors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.GRAY};//保存各种颜色信息


    public Snake(SnakeViewInterface snakeView, int flag, boolean isAI, String name) {
        this.snakeView = snakeView;
        this.flag = flag;
        this.isAI = isAI;
        if (isAI) {
            this.name = "AI蛇" + flag;
        } else {
            this.name = name;
        }
        init();
    }

    private void init() {
        snake = Collections.synchronizedList(new ArrayList<>());
        snakes = Collections.synchronizedList(new ArrayList<>());
        foods = Collections.synchronizedList(new ArrayList<>());

        //随机生成贪吃蛇头部的位置
        Random random = new Random();
        float sx = random.nextInt(Constant.viewWidth - Constant.nullWidth - Constant.snake_d) + Constant.nullWidth + Constant.snake_d;
        float sy = random.nextInt(Constant.viewHeight - Constant.nullWidth - Constant.snake_d) + Constant.nullWidth + Constant.snake_d;

        eat = Constant.snake_score * 5;
        if (!isAI) {//如果玩家操控的是贪吃蛇（不是AI）重新设置在屏幕中央
            sx = (Constant.viewWidth + Constant.nullWidth * 2) / 2 - Constant.snake_d;
            sy = (Constant.viewHeight + Constant.nullWidth * 2) / 2 - Constant.snake_d;
        }

        //生成头部节点
        head = new Point(sx, sy);
        //生成方向点
        opt = new Point(Constant.snake_len, 0);

        //生成五个节点构成贪吃蛇的身体
        for (int i = 0; i < 5; ++i) {
            aiOpt();//随机生成方向
            sx += opt.getX();
            sy += opt.getY();
            while (isOut(sx, sy)) {//若果新生成的点的坐标出界，在反方向生成一个点
                sx -= opt.getX();
                sy -= opt.getY();
                aiOpt();
            }
            snake.add(0, new Point(sx, sy));//将生成的节点添加到当前贪吃蛇的列表中
        }
        //随机生成贪吃蛇的颜色
        int color = random.nextInt(colors.length);
        //设置贪吃蛇颜色
        snakeColor = colors[color];
        aiOpt();
    }

    /**
     * 返回此贪吃蛇
     *
     * @return 保存贪吃蛇身体点的列表
     */
    public List<Point> getSnake() {
        List<Point> tSnake = Collections.synchronizedList(new ArrayList<>());
        tSnake.addAll(snake);
        return tSnake;
    }

    /**
     * 移动贪吃蛇
     */
    public void move() {
        if (!isDeath) {//首先判断贪吃蛇是否死亡
            if (isAI && speed == 1) {//如果是AI，并且速度为1（没有在吃蛇的尸体）
                Random random = new Random();
                int change = random.nextInt(200);
                if (change % 101 == 0) {//二百分之一的几率改变方向
                    aiOpt();
                }
                if (change % 2 == 0) {
                    searchMove();//避免贪吃蛇与边界或其他贪吃蛇发生碰撞
                }
            }
            for (int i = 0; i < speed; ++i) {//按蛇的速度设置循环次数
                if (isDeath) { //贪吃蛇死亡，退出循环
                    break;
                }
                if (isEat < Constant.snake_score) {//吃到的分数不足以生成一个新的节点
                    snake.remove(0);//将尾部节点（第一个节点）删除
                } else {//吃到的分数足以生成一个新的节点
                    isEat -= Constant.snake_score;//将分数减去一个节点所代表的分数
                    length++;//贪吃蛇长度加一
                }
                //将要到达的点设置为头部节点
                head.setX(head.getX() + opt.getX());
                head.setY(head.getY() + opt.getY());
                //将新的头部节点添加到此贪吃蛇中
                snake.add(new Point(head.getX(), head.getY()));
                isDeath = judgeIsDeath();//判断是否死亡
            }
            if (isAI) {//是AI将蛇的速度设置为1
                speed = 1;
            }
        }
    }

    /**
     * AI蛇自动避免与边界或者其他贪吃蛇发生碰撞
     */
    private void searchMove() {
        snakes = snakeView.getSnakesInView();//从视图中获取所有贪吃蛇
        boolean isok = false;
        int c = 0;
        while (!isok) {
            c++;
            if (c == 5) {//循环搜索5次
                break;
            }

            //设置贪吃蛇头部节点将要到达的点
            float thisx = head.getX() + opt.getX() + Constant.snake_d / 2;
            float thisy = head.getY() + opt.getY() + Constant.snake_d / 2;

            if (thisx <= 20 + Constant.nullWidth || thisx >= Constant.viewWidth - 20 - Constant.nullWidth || thisy <=
                    20 + Constant.nullWidth || thisy >= Constant.viewHeight - 20 - Constant.nullWidth) {//如果头部将要到达的点离边界距离小于20，改变贪吃蛇方向
                aiOpt();
                continue;
            }
            boolean is = true;//是否继续循环
            for (int i = 0; i < snakes.size(); ++i) {//循环遍历每条贪吃蛇，避免两只蛇发生碰撞
                Snake tsnake = snakes.get(i);
                if (tsnake.flag == this.flag || tsnake.isDeath) {//如果是本条贪吃蛇，或者贪吃蛇已经死亡
                    continue;
                }
                for (Point point : tsnake.getSnake()) {//循环查找到的贪吃蛇的每一个节点
                    if (judge(new Point(thisx, thisy), point, Constant.snake_d / 2, false)) {
                        i = snakes.size();//终止内层循环
                        is = false;//方向改变，终止外层循环
                        aiOpt();//随机改变贪吃蛇方向
                        break;
                    }
                }
            }
            if (is) {
                isok = true;
            }
        }
    }

    /**
     * 随机改变贪吃蛇方向
     */
    private void aiOpt() { //随机生成一个方向
        Random random = new Random();
        int rx = random.nextInt(100);//随机生成数大小
        int ry = random.nextInt(100);
        int fx = random.nextInt(100);//fx，fy随机生成数字控制贪吃蛇前后方向
        int fy = random.nextInt(100);
        if (fx % 2 == 0) {
            rx *= -1;
        }
        if (fy % 2 == 0) {
            ry *= -1;
        }
        if (rx == 0 && ry == 0) {
            rx++;
        }
        float optf = (float) Math.sqrt(rx * rx + ry * ry);//勾股定理
        opt.setX(Constant.snake_len * rx / optf);//计算比例并设置点
        opt.setY(Constant.snake_len * ry / optf);
    }

    /**
     * 判断两点是否相碰
     *
     * @param p1     点1
     * @param p2     贪吃蛇身体点
     * @param r      点的半径
     * @param isFood 是否是食物
     * @return 相碰返回true，不相碰返回false
     */
    private boolean judge(Point p1, Point p2, int r, boolean isFood) {

        Point point = new Point(p1.getX() - (p2.getX() + r), p1.getY() - (p2.getY() + r));//新生成一个点，来表示两个点之间的距离
        float pLen = (float) Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());//计算两点之间的距离
        if (isFood) {//如果是食物，距离减去半径的3/2
            pLen -= r * 3 / 2;
        }
        if (pLen <= r + Constant.snake_d / 2) {
            return true;
        }

        return false;
    }

    /**
     * 判断节点是否出界
     *
     * @param x 节点x坐标
     * @param y 节点y坐标
     * @return 出界true不出界false
     */
    private boolean isOut(float x, float y) { //判断当前节点是否出界
        if (x <= Constant.nullWidth + Constant.snake_d / 2 ||    //撞到左侧墙壁
                x >= Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2 ||  //撞到右侧墙壁
                y <= Constant.nullWidth + Constant.snake_d / 2 ||   //撞到上侧墙壁
                y >= Constant.viewHeight + Constant.nullWidth - Constant.snake_d / 2) { //撞到下侧墙壁
            return true;
        }
        return false;
    }

    /**
     * 判断贪吃蛇是否死亡
     *
     * @return 死亡true，存活false
     */
    private boolean judgeIsDeath() {
        snakes = snakeView.getSnakesInView();
        foods = snakeView.getFoods();

        //头部节点位置
        float thisx = head.getX() + Constant.snake_d / 2;
        float thisy = head.getY() + Constant.snake_d / 2;

        //判断是否碰到边界，若出界，将点设置在边界内
        if (isOut(thisx, thisy)) {
            Point p = this.snake.get(snake.size() - 1);//得到头部节点
            this.snake.remove(snake.size() - 1);
            if (p.getX() <= Constant.nullWidth + Constant.snake_d / 2) {
                p.setX(Constant.nullWidth + Constant.snake_d / 2);
            } else if (p.getX() >= Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2) {
                p.setX(Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2);
            }
            if (p.getY() <= Constant.nullWidth + Constant.snake_d / 2) {
                p.setY(Constant.nullWidth + Constant.snake_d / 2);
            } else if (p.getY() >= Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2) {
                p.setY(Constant.viewHeight + Constant.nullWidth - Constant.snake_d / 2);
            }
            this.snake.add(new Point(p.getX(), p.getY()));
            head.setX(p.getX());
            head.setY(p.getY());
            return true;
        }

        //吃食物
        int fSize = foods.size();
        for (int i = 0; i < fSize; ++i) {
            Food food = foods.get(i);
            if (food == null) {
                continue;
            }
            if (judge(new Point(thisx, thisy), new Point(food.getX(), food.getY()), Constant.snake_d / 6, true)) {
                isEat += Constant.snake_score / 15;
                eat += Constant.snake_score / 15;
                foods.remove(i);
                fSize--;
                snakeView.removeFood(i);//删除当前食物
            }
        }


        for (int i = 0; i < snakes.size(); ++i) {
            Snake tsnake = snakes.get(i);
            if (tsnake.flag == this.flag) {//是当前蛇，继续遍历下一条
                continue;
            }
            if (tsnake.isDeath) { //如果遍历到的蛇已经死亡
                int size = tsnake.getSnake().size(); //死亡蛇的结点总数
                for (int k = 0; k < size; ++k) {
                    Point p = tsnake.getSnake().get(k);//得到结点
                    if (judge(new Point(thisx, thisy), p, Constant.snake_d / 4, true)) {//贪吃蛇吃到尸体
                        //加分
                        isEat += Constant.snake_score / 3;
                        eat += Constant.snake_score / 3;
                        if (isAI) { //如果是AI，加速吃掉蛇的尸体
                            if (size > 1) {
                                speed = 2;//AI加速
                                int f = k;//f记录位置，控制前进方向，向剩余多的方向前进
                                if (f < size / 2) {
                                    f += 1;
                                } else {
                                    f -= 1;
                                }
                                //将尸体下一个节点的位置设置为当前贪吃蛇的前进方向
                                p = tsnake.getSnake().get(f);
                                int rx = (int) (p.getX() - head.getX());
                                int ry = (int) (p.getY() - head.getY());
                                float optf = (float) Math.sqrt(rx * rx + ry * ry);
                                opt.setX(Constant.snake_len * rx / optf);
                                opt.setY(Constant.snake_len * ry / optf);
                            }
                        }
                        tsnake.getSnake().remove(k);//删除被吃掉的节点
                        size--;//长度减1
                        snakeView.removeSnake(tsnake.flag, k);//删除被吃掉的节点
                    }
                }
                continue;
            }
            //判断贪吃蛇头部是否与其他蛇相碰，相碰则死亡
            for (Point point : tsnake.getSnake()) {
                if (judge(new Point(thisx, thisy), point, Constant.snake_d / 2, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移除贪吃蛇的某个点（尸体被吃掉）
     *
     * @param k
     */
    public void removeSnake(int k) {
        this.snake.remove(k);
    }

    /**
     * 设置贪吃蛇运动方向
     *
     * @param opt 标志运动方向的点
     */
    public void setOpt(Point opt) {
        this.opt = opt;
    }

    /**
     * 设置贪吃蛇的速度
     *
     * @param speed 速度
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public SnakeBean getSnakeBean() {
        SnakeBean snakeBean = new SnakeBean();
        snakeBean.head = this.head;
        snakeBean.snake = this.snake;
        snakeBean.eat = this.eat;
        snakeBean.isDeath = this.isDeath;
        snakeBean.snakeColor = this.snakeColor;
        snakeBean.flag = this.flag;
        snakeBean.name = this.name;
        return snakeBean;
    }
}
