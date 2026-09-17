package com.winter.context;

import com.winter.annotation.Snowball;
import com.winter.annotation.PostSnowball;
import java.lang.reflect.Method;
import com.winter.registry.SnowballRegistry;
import com.winter.scanner.ClassScanner;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class WinterContext {

    private final SnowballRegistry registry;
    private final ClassScanner scanner;
    private final String basePackage;
    private final Set<Class<?>> creatingSnowballs;
    private final Set<Object> initializedSnowballs;

    public WinterContext(String basePackage) {
        this.basePackage = basePackage;
        this.registry = new SnowballRegistry();
        this.scanner = new ClassScanner();
        this.creatingSnowballs = new HashSet<>();
        this.initializedSnowballs = new HashSet<>();
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

    private Object createSnowball(Class<?> clazz) {

        // اگر قبلاً ساخته شده، همان instance را برگردان
        Object existing = registry.get(clazz);

        if (existing != null) {
            return existing;
        }

        // تشخیص Circular Dependency
        if (creatingSnowballs.contains(clazz)) {
            throw new RuntimeException(
                    "Circular dependency detected: "
                            + clazz.getName()
            );
        }

        // علامت بزن که این Snowball در حال ساخته شدن است
        creatingSnowballs.add(clazz);

        try {

            // پیدا کردن constructor مناسب
            Constructor<?> constructor =
                    findConstructor(clazz);

            // پیدا کردن dependencyها
            Class<?>[] parameterTypes =
                    constructor.getParameterTypes();

            Object[] dependencies =
                    new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {

                Class<?> dependencyType =
                        parameterTypes[i];

                // Dependency باید Snowball باشد
                if (!dependencyType.isAnnotationPresent(
                        Snowball.class)) {

                    throw new RuntimeException(
                            "Dependency is not a Snowball: "
                                    + dependencyType.getName()
                    );
                }

                // ساخت یا دریافت dependency
                dependencies[i] =
                        createSnowball(dependencyType);
            }

            // ساخت Snowball با dependencyهای resolve شده
            Object snowball =
                    constructor.newInstance(dependencies);

            registry.register(clazz, snowball);

            initializeSnowball(snowball);

            return snowball;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Could not create Snowball: "
                            + clazz.getName(),
                    e
            );

        } finally {

            // ساخت تمام شد؛ دیگر در حال ساخت نیست
            creatingSnowballs.remove(clazz);
        }
    }

    private Constructor<?> findConstructor(Class<?> clazz) {

        Constructor<?>[] constructors =
                clazz.getDeclaredConstructors();

        if (constructors.length != 1) {
            throw new RuntimeException(
                    "Snowball must have exactly one constructor: "
                            + clazz.getName()
            );
        }

        return constructors[0];
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

    private void initializeSnowball(Object snowball) {

        // اگر قبلاً lifecycle آن اجرا شده، دوباره اجرا نکن
        if (initializedSnowballs.contains(snowball)) {
            return;
        }

        Class<?> clazz = snowball.getClass();

        for (Method method :
                clazz.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(
                    PostSnowball.class)) {
                continue;
            }

            try {

                method.setAccessible(true);
                method.invoke(snowball);

            } catch (Exception e) {

                throw new RuntimeException(
                        "Could not initialize Snowball: "
                                + clazz.getName(),
                        e
                );
            }
        }

        // فقط بعد از اجرای موفق callback ثبتش کن
        initializedSnowballs.add(snowball);
    }
}