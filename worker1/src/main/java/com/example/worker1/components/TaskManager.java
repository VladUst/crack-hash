package com.example.worker1.components;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TaskManager {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    public TaskManager() {
        new Thread(() -> {
            while (true) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void addRequestTask(Runnable task) {
        taskQueue.offer(task);
    }
}
