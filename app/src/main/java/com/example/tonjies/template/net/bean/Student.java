package com.example.tonjies.template.net.bean;

import com.example.tonjies.template.annotation.Double;


/**
 * Created by 舍长 on 2019/1/20
 * describe:
 */
public class Student extends User {
    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
