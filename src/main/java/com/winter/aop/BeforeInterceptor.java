package com.winter.aop;

import com.winter.annotation.Before;

import java.lang.reflect.Method;

public class BeforeInterceptor
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

            if (!method.isAnnotationPresent(Before.class)) {
                continue;
            }

            Before before =
                    method.getAnnotation(Before.class);

            if (!before.value().equals(
                    targetMethod.getName())) {
                continue;
            }

            method.setAccessible(true);
            method.invoke(target);
        }

        return invocation.proceed();
    }
}