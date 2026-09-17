package com.winter.proxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static Object createProxy(Object target) {

        Class<?>[] interfaces =
                target.getClass().getInterfaces();

        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                new WinterInvocationHandler(target)
        );
    }
}