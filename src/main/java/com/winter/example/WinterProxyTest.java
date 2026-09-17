package com.winter.example;

import com.winter.context.WinterContext;

public class WinterProxyTest {

    public static void main(String[] args) {

        WinterContext context =
                new WinterContext("com.winter.example");

        context.start();

        ZooService zoo =
                context.getSnowball(ZooService.class);

        zoo.testCat();
    }
}