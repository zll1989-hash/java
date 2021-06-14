package com.sankuai.inf.leaf.server.futurethread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * @Author Mike
 * @create 2021/6/6 11:46
 */
public class CompletableFutureCallBack {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        System.out.println(Thread.currentThread().getName());

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("child run thread :" + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 123;
        }, executorService);

        System.out.println("parent end!!!!");

        try {
            future.whenCompleteAsync(new BiConsumer<Integer, Throwable>() {
                @Override
                public void accept(Integer integer, Throwable throwable) {
                    System.out.println("result do result:" + Thread.currentThread().getName());
                    System.out.println("=======run integer:"+integer*10);
                    System.out.println("special job run");
                    executorService.shutdown();
                }
            }, executorService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
