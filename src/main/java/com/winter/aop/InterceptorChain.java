package com.winter.aop;

import java.util.List;

public class InterceptorChain {

    private final List<MethodInterceptor> interceptors;

    private int index = 0;

    public InterceptorChain(
            List<MethodInterceptor> interceptors
    ) {
        this.interceptors = interceptors;
    }

    public Object proceed(
            MethodInvocation invocation
    ) throws Throwable {

        if (index < interceptors.size()) {

            MethodInterceptor interceptor =
                    interceptors.get(index++);

            return interceptor.invoke(invocation);
        }

        return invocation
                .getMethod()
                .invoke(
                        invocation.getTarget(),
                        invocation.getArgs()
                );
    }
}