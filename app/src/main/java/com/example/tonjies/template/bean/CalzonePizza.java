package com.example.tonjies.template.bean;

import com.example.lib_annotations.annotation.Factory;

@Factory(
        id = "CalzonePizza",
        type = Meal.class
)
public class CalzonePizza implements Meal {
    @Override
    public float getPrice() {
        return 8.5f;
    }
}
