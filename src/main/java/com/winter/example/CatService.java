package com.winter.example;

import com.winter.annotation.Before;
import com.winter.annotation.Snowball;
import com.winter.example.services.FoodService;
import com.winter.annotation.After;
import com.winter.annotation.Around;
import com.winter.aop.MethodInvocation;
@Snowball
public class CatService implements CatOperations {

    private final FoodService foodService;

    public CatService(FoodService foodService) {
        this.foodService = foodService;
    }

    @Before("meow")
    public void beforeMeow() {
        System.out.println("Before Meow!");
    }

    @Override
    public void meow() {
        System.out.println("Meow!");
        foodService.feed();
    }
    @After("meow")
    public void afterMeow() {
        System.out.println("After Meow!");
    }
    public FoodService getFoodService() {
        return foodService;
    }

    @Around("meow")
    public Object aroundMeow(
            MethodInvocation invocation
    ) throws Throwable {

        System.out.println("Around Before");

        Object result =
                invocation.proceed();

        System.out.println("Around After");

        return result;
    }

    @Override
    public String name() {
        return "Milo";
    }

    @Around("name")
    public Object aroundName(
            MethodInvocation invocation
    ) throws Throwable {

        System.out.println("Around Before");

        Object result =
                invocation.proceed();

        System.out.println("Real result: " + result);

        return "Snow-" + result;
    }
}