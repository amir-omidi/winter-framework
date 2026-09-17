package com.winter.context;

import com.winter.annotation.Snowball;
import com.winter.registry.SnowballRegistry;
import com.winter.scanner.ClassScanner;

import java.util.List;

public class WinterContext {

    private final SnowballRegistry registry;
    private final ClassScanner scanner;
    private final String basePackage;

    public WinterContext(String basePackage) {
        this.basePackage = basePackage;
        this.registry = new SnowballRegistry();
        this.scanner = new ClassScanner();
    }

    public void start() {

        List<Class<?>> classes =
                scanner.scan(basePackage);

        for (Class<?> clazz : classes) {

            if (!clazz.isAnnotationPresent(Snowball.class)) {
                continue;
            }

            createSnowball(clazz);
        }
    }

    private void createSnowball(Class<?> clazz) {

        try {

            Object snowball =
                    clazz.getDeclaredConstructor()
                            .newInstance();

            registry.register(clazz, snowball);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Could not create Snowball: "
                            + clazz.getName(),
                    e
            );
        }
    }

    public <T> T getSnowball(Class<T> type) {

        Object snowball =
                registry.get(type);

        if (snowball == null) {
            throw new RuntimeException(
                    "Snowball not found: "
                            + type.getName()
            );
        }

        return type.cast(snowball);
    }
}