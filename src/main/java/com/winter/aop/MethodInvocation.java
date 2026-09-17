package com.winter.aop;

import java.lang.reflect.Method;

public class MethodInvocation {

    private final Object target;
    private final Method method;
    private final Object[] args;

    private final InterceptorChain chain;

    public MethodInvocation(
            Object target,
            Method method,
            Object[] args,
            InterceptorChain chain
    ) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.chain = chain;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object proceed() throws Throwable {
        return chain.proceed(this);
    }
}