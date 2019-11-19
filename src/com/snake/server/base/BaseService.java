package com.snake.server.base;

public abstract class BaseService {
    private MyThread thread;
    private boolean running = true;

    public BaseService(){
        thread = new MyThread();
        thread.start();
    }

    protected abstract void logic();
    protected abstract void init();

    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("BaseService before SnakeView init");
            init();
            while (running) {
                System.out.println("BaseService before SnakeView logic");
                logic();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("BaseService after SnakeView logic");
            }
        }
    }
}
