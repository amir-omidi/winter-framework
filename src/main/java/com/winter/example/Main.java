package com.winter.example;

import com.winter.annotation.Snowball;
import com.winter.context.WinterContext;
import com.winter.example.services.DogService;
import com.winter.scanner.ClassScanner;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ClassScanner scanner = new ClassScanner();

        WinterContext context =
                new WinterContext(
                        "com.winter.example"
                );

        context.start();

        CatService cat =
                context.getSnowball(CatService.class);

        DogService dog =
                context.getSnowball(DogService.class);

        cat.meow();
        dog.bark();


    }

}
