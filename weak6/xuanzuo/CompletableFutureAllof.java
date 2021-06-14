package com.sankuai.inf.leaf.server.futurethread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author Mike
 * @create 2021/6/6 13:53
 */
public class CompletableFutureAllof {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            int f1 = new Random().nextInt(100);
            System.out.println("f1 value：" + f1);
            return f1;
        }, executorService);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int f2 = new Random().nextInt(100);
            System.out.println("f2 value：" + f2);
            return f2;
        }, executorService);

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int f3 = new Random().nextInt(100);
            System.out.println("f3 value：" + f3);
            return f3;
        }, executorService);
        List<CompletableFuture<Integer>> list = new ArrayList<>();
        list.add(future1);
        list.add(future2);
        list.add(future3);
        CompletableFuture<Void> all = CompletableFuture.allOf(list.toArray(new CompletableFuture[]{}));
        all.thenRunAsync(() -> {
            AtomicReference<Integer> result = new AtomicReference<>(0);
            list.stream().forEach(future->{
                try {
                    Integer value = future.get();
                    result.updateAndGet(v->v+value);
                    System.out.println("=====output result===="+result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        });
    }
}
