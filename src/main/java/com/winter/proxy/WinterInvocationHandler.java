package com.winter.proxy;

import com.winter.aop.AfterInterceptor;
import com.winter.aop.AroundInterceptor;
import com.winter.aop.BeforeInterceptor;
import com.winter.aop.InterceptorChain;
import com.winter.aop.MethodInterceptor;
import com.winter.aop.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class WinterInvocationHandler
        implements InvocationHandler {

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

        List<MethodInterceptor> interceptors =
                List.of(
                        new AroundInterceptor(),
                        new BeforeInterceptor(),
                        new AfterInterceptor()
                );

        InterceptorChain chain =
                new InterceptorChain(interceptors);

        MethodInvocation invocation =
                new MethodInvocation(
                        target,
                        method,
                        args,
                        chain
                );

        return invocation.proceed();
    }
}