package com.sankuai.inf.leaf.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Lucien
 * @create 2021/6/3 13:38
 * <p>
 * Lambda表达式是Java8中非常重要的一个新特性，
 * 其基于函数式编程的思想，支持将代码作为方法参数进行使用。可
 * 以把Lambda表达式理解为通过一种更加简洁的方式表示可传递的匿名函数。
 * (参数列表) -> {方法体}
 * 方法体：用于执行业务逻辑。
 * 可以是单一语句，也可以是语句块。
 * 如果是单一语句，可以省略花括号。
 * 当需要返回值，
 * 如果方法体中只有一条语句，
 * 可以省略return，
 * 会自动根据结果进行返回。
 * <p>
 * (int x,int y) ->{System.out.println(x)System.out.println(x);return x+y;}
 */
public class StudentTest {

    /**
     * @param studentList
     * @param value
     * @param flag
     * @return
     */

    public static Student getStudentInfo(List<Student> studentList, String value, String flag) {
        for (Student student : studentList) {
            if ("name".equals(flag)) {
                if (value.equals(student.getName())) {
                    return student;
                }
            }
            if ("sex".equals(flag)) {
                if (value.equals(student.getSex())) {
                    return student;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "张三", "M"));
        students.add(new Student(2, "李四", "M"));
        students.add(new Student(1, "王五", "M"));

        Student studentInfo = getStudentInfo(students, "张三", "name");
        System.out.println(studentInfo);

        String[] language = {"c", "c++", "c#", "java", "python", "go", "hive", "php"};

        //旧的循环比较方式
        Arrays.sort(language, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o1.compareTo(o2));
            }
        });
        Arrays.sort(language,(o1,o2)->(o1.compareTo(o2)));
    }

}
