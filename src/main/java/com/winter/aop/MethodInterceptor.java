package com.winter.aop;

public interface MethodInterceptor {

    Object invoke(
            MethodInvocation invocation
    ) throws Throwable;
}