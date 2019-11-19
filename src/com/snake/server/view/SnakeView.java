package com.snake.server.view;

import com.snake.common.ClientData;
import com.snake.common.Constant;
import com.snake.common.bean.Food;
import com.snake.common.bean.SnakeBean;
import com.snake.server.StartInterface;
import com.snake.server.base.BaseService;
import com.snake.server.base.Color;
import com.snake.server.bean.Snake;

import java.io.Serializable;
import java.util.*;

public class SnakeView extends BaseService implements SnakeViewInterface {
    private static final long serialVersionUID = -94956378890701786L;
    private List<Snake> snakes;
    private List<Food> foods;
    private int sFlag = 1;
    private int[] colors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.GRAY};
    private String rank;
    private List<SnakeBean> snakeBeans;
    private List<Food> foodsData;
    private StartInterface socketServer;

    private int snakeNum = 30;

    public SnakeView() {
        super();
    }

    @Override
    protected void logic() {
        System.out.println("SnakeView logic" + System.currentTimeMillis());
        int size = snakes.size();
        for (int i = 0; i < size; ++i) {
            Snake snake = snakes.get(i);
            if (snake.getSnake().size() == 0) {//贪吃蛇的长度为零（死亡且全部被其他蛇吃掉），移除当前蛇，并新添加一条AI
                snakes.remove(snake);
                snakes.add(new Snake(this, sFlag++, true, ""));//
            } else {
                snake.move();
            }
        }
        System.out.println("SnakeView logic before sort" + System.currentTimeMillis());
        List<Snake> tSnakes = Collections.synchronizedList(new ArrayList<>());
        tSnakes.addAll(snakes);
        //按照分数排序
        Collections.sort(tSnakes, new Comparator<Snake>() {
            @Override
            public int compare(Snake o1, Snake o2) {
                return ((Integer) o2.eat).compareTo((o1.eat));
            }
        });

        int fSize = foods.size();
        for (int i = 0; i < fSize; ++i) {
            Food food = foods.get(i);
            if (food.isNull()) {
                foods.remove(i);
                addFood();
            }
        }

        //排名
        StringBuilder builder = new StringBuilder();
        int tSize = 5;
        int j = 0;//用j记录排名
        for (int i = 1; i <= tSize; ++i) {
            if (tSnakes.get(i - 1).isDeath) {//
                tSize++;
                if (tSize > snakeNum - 1) {
                    break;
                }
            } else {
                j++;
                builder.append("\n" + j + "--" + tSnakes.get(i - 1).name + "--" + tSnakes.get(i - 1).eat);
            }
        }
        rank = builder.toString();
        System.out.println(rank);
        snakeBeans = getSnakes();
        foodsData = getFoods();
        socketServer.sendToAllClient();
    }

    /**
     * 初始化界面，向界面中添加贪吃蛇和食物
     */
    @Override
    protected void init() {
        System.out.println("SnakeView init");
        snakes = Collections.synchronizedList(new ArrayList<>());
        foods = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < Constant.snake_num; ++i) {
            snakes.add(new Snake(this, sFlag++, true, ""));
        }
        addFood();
    }

    /**
     * 添加食物，随机生成食物的位置和颜色
     */
    private void addFood() {
        Random random = new Random();
        int fNum = Constant.food_num - foods.size();
        for (int i = 0; i < fNum; ++i) {
            int x = random.nextInt(Constant.viewWidth - Constant.nullWidth / 2) + Constant.snake_d + Constant.nullWidth;
            int y = random.nextInt(Constant.viewHeight - Constant.nullWidth / 2) + Constant.snake_d + Constant.nullWidth;
            int c = random.nextInt(colors.length);
            foods.add(new Food(x, y, colors[c]));
        }
    }

    /**
     * @return
     */
    @Override
    public List<SnakeBean> getSnakes() {
        List<SnakeBean> snakeBeans = Collections.synchronizedList(new ArrayList<>());
//        snakeBeans.addAll(snakes);
//            <SnakeBean> snakeBeans = new ArrayList<>();
        for (Snake snake : snakes) {
            snakeBeans.add(snake.getSnakeBean());
        }
        return snakeBeans;
    }

    public List<Snake> getSnakesInView() {
        Vector<Snake> tSnakes = new Vector<>();
        tSnakes.addAll(snakes);
        return tSnakes;
    }


    @Override
    public List<Food> getFoods() {
        Vector<Food> tFoods = new Vector<>();
        tFoods.addAll(foods);
        return tFoods;
    }

    /**
     * 删除死亡的贪吃蛇被吃掉的节点
     *
     * @param flag 蛇的标记
     * @param k    蛇被吃的位置
     */
    @Override
    public void removeSnake(int flag, int k) {
        for (Snake snake : snakes) {
            if (snake.flag == flag) {
                snake.removeSnake(k);
                break;
            }
        }
    }

    @Override
    public void removeFood(int k) {
        foods.get(k).setNull(true);
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public void setData(ClientData clientData) {
        for (Snake snake : snakes) {
            if (snake.flag == clientData.getFlag()) {
                snake.setOpt(clientData.getOpt());
                snake.setSpeed(clientData.getSpeed());
            }
        }
    }

    @Override
    public List<Food> getFoodsData() {
        return foodsData;
    }

    @Override
    public List<SnakeBean> getSnakeBeans() {
        return snakeBeans;
    }

    @Override
    public void addSnake(int flag, boolean isAI, String name) {
        snakes.add(new Snake(this, flag, isAI, name));
        snakeNum++;
    }

    public int getsFlag() {
        return sFlag;
    }

    public void setsFlag(int sFlag) {
        this.sFlag = sFlag;
    }

    public StartInterface getSocketServer() {
        return socketServer;
    }

    public void setSocketServer(StartInterface socketServer) {
        this.socketServer = socketServer;
    }
}
