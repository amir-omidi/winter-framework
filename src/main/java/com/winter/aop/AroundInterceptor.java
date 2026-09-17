package com.winter.aop;

import com.winter.annotation.Around;

import java.lang.reflect.Method;

public class AroundInterceptor
        implements MethodInterceptor {

    @Override
    public Object invoke(
            MethodInvocation invocation
    ) throws Throwable {

        Object target =
                invocation.getTarget();

        Method targetMethod =
                invocation.getMethod();

        for (Method method :
                target.getClass().getDeclaredMethods()) {

            if (!method.isAnnotationPresent(Around.class)) {
                continue;
            }

            Around around =
                    method.getAnnotation(Around.class);

            if (!around.value().equals(
                    targetMethod.getName())) {
                continue;
            }

            method.setAccessible(true);

            return method.invoke(
                    target,
                    invocation
            );
        }

        return invocation.proceed();
    }
}