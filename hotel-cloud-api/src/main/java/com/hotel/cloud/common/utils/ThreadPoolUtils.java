package com.hotel.cloud.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolUtils {

    private final static ExecutorService SERVICE;

    static {
        SERVICE = Executors.newCachedThreadPool();
    }


    public static void execute(Runnable task) {
        SERVICE.execute(task);
    }

    /**
     * 非阻塞的线程池
     *
     * @param tasks
     * @param <V>
     * @return
     */
    public static <V> List<V> execute(List<Callable<V>> tasks) {
        ExecutorCompletionService<V> completionService = new ExecutorCompletionService<>(SERVICE);
        for (Callable<V> task : tasks) {
            completionService.submit(task);
        }
        List<V> result = new ArrayList<>(tasks.size());

        for (int i = 0; i < tasks.size(); i++) {
            try {
                Future<V> future = completionService.take();
                System.out.println("execute:" + future.get());
                result.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
