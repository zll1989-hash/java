package com.sankuai.inf.leaf.server.futurethread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Mike
 * @create 2021/6/6 13:27
 */
public class CompletableFutureJoin {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        CompletableFuture  future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("======future1====="+Thread.currentThread().getName());
            return "hello";
        },executorService);

        CompletableFuture future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("======future2===="+Thread.currentThread().getName());
            return "lucien";
        },executorService);

        CompletableFuture future = future1.thenCombineAsync(future2,(f1,f2)->{
            System.out.println("===========future====="+Thread.currentThread().getName());
            return  f1+","+f2;
        },executorService);

        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }




}
