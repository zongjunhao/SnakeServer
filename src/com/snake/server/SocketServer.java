package com.snake.server;

import com.alibaba.fastjson.JSON;
import com.snake.common.ClientData;
import com.snake.common.MyObjectOutputStream;
import com.snake.common.ServerData;
import com.snake.common.User;
import com.snake.common.bean.Food;
import com.snake.common.bean.SnakeBean;
import com.snake.server.view.SnakeView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class SocketServer implements StartInterface {
    //    private static User user = new User();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SnakeView snakeView;
    private List<SendClass> threads = new ArrayList<>();
    private static List<SnakeBean> snakeBeans = null;
    private static List<Food> foods = null;
    private static String rank = "";

    public static void main(String[] args) {
        System.out.println("main");
        SocketServer socketServer = new SocketServer();
        socketServer.startService();
    }

//    private static void startService() {
//        try {
//            //创建ServerSocket
//            ServerSocket serverSocket = new ServerSocket(9999);
//            System.out.println("--开启服务器，监听端口9999--");
//            snakeView = new SnakeView();
//            //监听端口，等待客户端连接
//            while (true) {
//                System.out.println("--等待客户端连接--");
//                Socket socket = serverSocket.accept();//等待客户端连接
//                System.out.println("得到客户端连接:" + socket);
//                int flag = snakeView.getsFlag();
//                snakeView.setsFlag(flag + 1);
//                synchronized (snakeView) {
//                    snakeView.addSnake(flag, false, "name");
//                }
//                readObject(socket);
//                sendObject(socket, flag);
////                sendMsg(socket, flag);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void startService() {
        try {
            //创建ServerSocket
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("--开启服务器，监听端口9999--");
            snakeView = new SnakeView();
            snakeView.setSocketServer(this);
            //监听端口，等待客户端连接
            while (true) {
                System.out.println("--等待客户端连接--");
                Socket socket = serverSocket.accept();//等待客户端连接
                System.out.println("得到客户端连接:" + socket);
                int flag = snakeView.getsFlag();
                snakeView.setsFlag(flag + 1);
                synchronized (snakeView) {
                    snakeView.addSnake(flag, false, getRandomString(6));
                }
                readObject(socket);
                this.threads.add(new SendClass(socket, flag));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private void readMsg(Socket socket) {
        new Thread(() -> {
            DataInputStream reader;
            try {
                //获取读取流
                reader = new DataInputStream(socket.getInputStream());
                while (true) {
                    System.out.println("*等待客户端输入*");
                    String msg = reader.readUTF();
                    System.out.println("获取到客户端的信息：" + System.currentTimeMillis() + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void readObject(Socket socket) {
        new Thread(() -> {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    System.out.println("*等待客户端输入对象*");
                    try {
                        ClientData clientData = (ClientData) ois.readObject();
//                    User user = (User) ois.readObject();
                        System.out.println(clientData.getFlag());
                        if (clientData.getFlag() == -1) {
                            socket.close();
                            System.out.println("客户端" + socket + "断开连接");
                            break;
                        }
//                    String msg = " id:" + user.getId() + " name:" + user.getName() + " age:" + user.getAge() + " time:" + sdf.format(new Date(user.getTime()));
                        System.out.println("获取到客户端的信息：接收时间:" + sdf.format(new Date(System.currentTimeMillis())) + "speed:" + clientData.getSpeed() + " flag:" + clientData.getFlag() + " opt:" + clientData.getOpt());
                        snakeView.setData(clientData);
                    } catch (ClassCastException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void sendObject(Socket socket, int flag) {
        new Thread(() -> {
            int time = 0;
            while (true) {
                try {
                    if (socket.isClosed()) break;
                    ObjectOutputStream oos = null;
                    if (time == 0) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        time++;
                    } else {
                        oos = new MyObjectOutputStream(socket.getOutputStream());
                    }
//                    oos.writeObject(new User("201721130058", "宗俊豪", 20));
                    synchronized (snakeView) {
                        ServerData serverData = new ServerData(snakeView.getSnakes(), snakeView.getFoods(), snakeView.getRank(), flag);
                        oos.writeObject(serverData);
                        oos.flush();
                        oos.reset();
                    }
                    System.out.println("发送对象");
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendMsg(Socket socket, int flag) {
        new Thread(() -> {
            while (true) {
                try {
                    if (socket.isClosed())
                        Thread.currentThread().stop();
                    DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
//                    ByteArrayOutputStream writer = new ByteArrayOutputStream(socket.getOutputStream());
                    ServerData serverData = new ServerData(snakeView.getSnakes(), snakeView.getFoods(), snakeView.getRank(), flag);
                    String msg = JSON.toJSONString(serverData);
//                    byte[] msgB = msg.getBytes();
//                    int len = msgB.length;
                    writer.writeUTF(msg); // 写一个UTF-8的信息
//                    writer.write(msgB, 0, len);
//                    writer.flush();
                    System.out.println("发送消息" + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public synchronized void sendToAllClient() {
        snakeBeans = snakeView.getSnakeBeans();
        foods = snakeView.getFoods();
        rank = snakeView.getRank();
        if (threads != null) {
            for (SendClass thread : threads) {
                System.out.println("before send" + System.currentTimeMillis());
                thread.send();
                System.out.println("after send" + System.currentTimeMillis());
            }
        }
    }

    class SendThread extends Thread {
        Socket socket = null;
        int flag;

        public SendThread(Socket socket, int flag) {
            this.socket = socket;
            this.flag = flag;
        }

        @Override
        public void run() {
            int time = 0;
            while (true) {
                try {
                    if (socket.isClosed())
                        break;
                    ObjectOutputStream oos = null;
                    if (time == 0) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        time++;
                    } else {
                        oos = new MyObjectOutputStream(socket.getOutputStream());
                    }
                    ServerData serverData = new ServerData(snakeView.getSnakes(), snakeView.getFoods(), snakeView.getRank(), flag);
                    oos.writeObject(serverData);
                    oos.flush();
                    oos.reset();
                    System.out.println("发送对象");
                    synchronized (this) {
                        this.wait();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    class SendClass {
        Socket socket = null;
        int flag;
        int time = 0;

        public SendClass(Socket socket, int flag) {
            this.socket = socket;
            this.flag = flag;
        }

        public void send() {
            try {
                if (!socket.isClosed()) {
                    ObjectOutputStream oos = null;
                    if (time == 0) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        time++;
                    } else {
                        oos = new MyObjectOutputStream(socket.getOutputStream());
                    }

                    ServerData serverData = new ServerData(snakeBeans, foods, rank, flag);
                    oos.writeObject(serverData);
                    oos.flush();
                    oos.reset();
                    System.out.println("发送对象");
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

