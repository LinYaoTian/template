package com.example.lib_annotations.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;



public class RandomUtil {
    private static final String RANDOM_SUFFIX = "_Random";

    private RandomUtil() {

    }

    /**
     * 通过反射的方式找到对应的辅助类，并调用对应的方法实现属性的注入
     * @param object 被注入的对象
     */
    public static void inject(Object object) {
        try {
            Class bindingClass = Class.forName(object.getClass().getCanonicalName() + RANDOM_SUFFIX);
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(object.getClass());
            constructor.newInstance(object);

        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InstantiationException e) {

        } catch (InvocationTargetException e) {

        }
    }
}

