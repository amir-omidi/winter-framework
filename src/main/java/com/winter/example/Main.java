package com.winter.example;

import com.winter.annotation.Snowball;
import com.winter.context.WinterContext;
import com.winter.example.services.DogService;
import com.winter.example.services.FoodService;
import com.winter.scanner.ClassScanner;



public class Main {
    public static void main(String[] args) {
        WinterContext context =
                new WinterContext(
                        "com.winter.example"
                );

        context.start();

        CatService cat =
                context.getSnowball(CatService.class);

        DogService dog =
                context.getSnowball(DogService.class);

        FoodService food =
                context.getSnowball(FoodService.class);

        cat.meow();
        dog.bark();

        System.out.println(
                cat.getFoodService() == food
        );

    }

}
