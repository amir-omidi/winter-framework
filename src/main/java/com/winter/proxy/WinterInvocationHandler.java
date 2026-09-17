package com.winter.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class WinterInvocationHandler implements InvocationHandler {

    private final Object target;

    public WinterInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(
            Object proxy,
            Method method,
            Object[] args
    ) throws Throwable {

        System.out.println(
                "[Winter] Before: " + method.getName()
        );

        Object result =
                method.invoke(target, args);

        System.out.println(
                "[Winter] After: " + method.getName()
        );

        return result;
    }
}