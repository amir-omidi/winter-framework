package com.winter.example.services;

import com.winter.annotation.PostSnowball;
import com.winter.annotation.Snowball;

@Snowball
public class FoodService {

    @PostSnowball
    public void init() {
        System.out.println("FoodService initialized!");
    }

    public void feed() {
        System.out.println("Food served!");
    }
}