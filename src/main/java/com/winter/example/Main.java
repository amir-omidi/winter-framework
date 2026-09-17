package com.winter.example;

import com.winter.context.WinterContext;


public class Main {
    public static void main(String[] args) {
        WinterContext context =
                new WinterContext("com.winter.example");

        context.start();

        CatOperations cat =
                context.getSnowball(CatOperations.class);

        cat.meow();


    }

}
