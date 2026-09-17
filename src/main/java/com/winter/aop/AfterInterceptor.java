package com.winter.aop;

import com.winter.annotation.After;

import java.lang.reflect.Method;

public class AfterInterceptor
        implements MethodInterceptor {

    @Override
    public Object invoke(
            MethodInvocation invocation
    ) throws Throwable {

        Object result =
                invocation.proceed();

        Object target =
                invocation.getTarget();

        Method targetMethod =
                invocation.getMethod();

        for (Method method :
                target.getClass().getDeclaredMethods()) {

            if (!method.isAnnotationPresent(After.class)) {
                continue;
            }

            After after =
                    method.getAnnotation(After.class);

            if (!after.value().equals(
                    targetMethod.getName())) {
                continue;
            }

            method.setAccessible(true);
            method.invoke(target);
        }

        return result;
    }
}