package com.snake.common;

public class Constant {
    public static String idAddress = "10.174.41.107";

    //用于区分信息显示区域
    public final static int GAME_VIEW_MARGIN = 1;
    public final static int SNAKE_LEN = 2;
    public final static int SNAKE_RANK = 3;
    public final static int PLAYER_ALIVE = 4;

    //用于标志玩家是否存活
    public static int playerAlive = 1;

    //屏幕的长和宽，初始设为0，将在应用初始化时进行重新设置
    public static int screenWidth = 1920;
    public static int screenHeight = 1080;
    //游戏的可视范围
    public static int viewWidth = 3840;
    public static int viewHeight = 2160;
    //游戏边框宽度
    public static int nullWidth = 100;
    //网格线间距
    public static int gridWidth = 25;
    //按钮边界
    public static int buttonMargin = 54;
    //蛇的每节身体的直径
    public static int snake_d = 54;
    //蛇前进的长度
    public static int snake_len = 25;
    //AI的数量
    public static int snake_num = 30;
    //食物的数量
    public static int food_num = 200;
    //蛇一节身体所代表的分数
    public static int snake_score = 75;

}
