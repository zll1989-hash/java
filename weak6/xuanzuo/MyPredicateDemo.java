package com.sankuai.inf.leaf.server.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @Author Lucien
 * @create 2021/6/3 19:45
 */
public class MyPredicateDemo {

    //predicate用法
    public static List<Student> filter(List<Student> studentList, Predicate<Student> predicate) {
        ArrayList<Student> list = new ArrayList<>();
        studentList.forEach(s -> {
            if (predicate.test(s)) {
                list.add(s);
            }
        });
        return list;
    }

    //consumer用法
    public static void foreach(List<String> arrays, Consumer<String> consumer) {
        arrays.forEach(s -> consumer.accept(s));
    }

    //function用法
    public static Integer convertInter(String value, Function<String, Integer> function) {
        return function.apply(value);
    }

    //supplier用法
    public static Integer getMin(Supplier<Integer> supplier) {
        return supplier.get();
    }

    public static void main(String[] args) {
        //predict
        System.out.println("=============predict begin===============");
        List<Student> students = new ArrayList<>();
        students.add(new Student(9, "cao", "M"));
        students.add(new Student(1, "张三", "M"));
        students.add(new Student(2, "李四", "M"));
        students.add(new Student(3, "王五", "F"));
        students.add(new Student(4, "liuliu", "M"));
        students.add(new Student(5, "fafa", "F"));
        students.sort(Comparator.comparing(Student::getId));
        System.out.println(students);
        System.out.println("------------------------------------");
        List<Student> result = filter(students, (s) -> s.getSex().equals("F"));
        students.sort(Comparator.comparing(Student::getId));
        System.out.println(result);
        System.out.println("==============predict end==============");
        //consumer
        System.out.println("==============consumer  begin==============");
        List<String> arrays = new ArrayList<>();
        arrays.add("java");
        arrays.add("python");
        arrays.add("go");
        arrays.add("hive");
        foreach(arrays, s -> {
            System.out.println(s + ",");
        });
        System.out.println("==============consumer  end==============");
        //function
        System.out.println("==============function  begin==============");
        String value = "666";
        Integer resultA = convertInter(value, (s) -> Integer.parseInt(s) + 222);
        System.out.println(resultA);
        System.out.println("==============function  end=================");
        //supplier
        System.out.println("==============supplier  begin=================");
        int[] arr = {100, 20, 50, 30, 99, 101, -50};
        Integer resultMin = getMin(()->{
            int min = arr[0];
            for(int i : arr){
                if(i<min){
                    min = i;
                }
            }
            return min;
        });
        System.out.println(resultMin);
        System.out.println("==============supplier  end=================");

        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock  readLock = readWriteLock.readLock();
        Lock  writeLock = readWriteLock.writeLock();
        readLock.lock();
        try {

        }catch (Exception e){

        }finally {
            readLock.unlock();
        }

    }

}
