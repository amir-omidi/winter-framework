package com.winter.registry;

import java.util.HashMap;
import java.util.Map;

public class SnowballRegistry {

    private final Map<Class<?>, Object> snowballs = new HashMap<>();

    public void register(Class<?> type, Object snowball) {
        snowballs.put(type, snowball);
    }

    public Object get(Class<?> type) {
        return snowballs.get(type);
    }
}