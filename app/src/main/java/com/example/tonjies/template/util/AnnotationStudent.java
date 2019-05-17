package com.example.tonjies.template.util;

import com.example.tonjies.template.annotation.MyDagger;
import com.example.tonjies.template.net.bean.Student;


import java.lang.reflect.Field;

public class AnnotationStudent {
    private static AnnotationStudent annotationStudent;
    public static AnnotationStudent instance(){
        synchronized (AnnotationStudent.class){
            if(annotationStudent == null){
                annotationStudent = new AnnotationStudent();
            }
            return annotationStudent;
        }
    }

    public void inject(Object o){
        Class<?> aClass = o.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field:declaredFields) {
            if(field.getName().equals("student") && field.isAnnotationPresent(MyDagger.class)) {
                MyDagger annotation = field.getAnnotation(MyDagger.class);
                Class<?> type = field.getType();
                if(Student.class.equals(type)) {
                    try {
                        field.setAccessible(true);
                        field.set(o, new Student(annotation.name(), annotation.size()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
