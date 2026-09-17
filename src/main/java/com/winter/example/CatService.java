package com.winter.example;

import com.winter.annotation.Snowball;
import com.winter.example.services.FoodService;

@Snowball
public class CatService {

    private final FoodService foodService;

    public CatService(FoodService foodService) {
        this.foodService = foodService;
    }

    public void meow() {
        System.out.println("Meow!");
        foodService.feed();
    }

    public FoodService getFoodService() {
        return foodService;
    }
}