package com.example.tonjies.template.bean;

import com.example.lib_annotations.annotation.Factory;

@Factory(
        id = "MargheritaPizza",
        type = Meal.class
)
public class MargheritaPizza implements Meal {
    @Override
    public float getPrice() {
        return 6.0f;
    }
}
