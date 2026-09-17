package com.winter.context;

import com.winter.annotation.Snowball;
import com.winter.annotation.PostSnowball;
import java.lang.reflect.Method;
import com.winter.registry.SnowballRegistry;
import com.winter.logging.WinterLogger;
import com.winter.scanner.ClassScanner;
import com.winter.proxy.ProxyFactory;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
public class WinterContext {

    private final SnowballRegistry registry;
    private final ClassScanner scanner;
    private final String basePackage;
    private final Set<Class<?>> creatingSnowballs;
    private final Set<Object> initializedSnowballs;
    private final Map<Class<?>, Class<?>> implementations;
    private final WinterLogger logger;
    public WinterContext(String basePackage) {
        this.basePackage = basePackage;
        this.registry = new SnowballRegistry();
        this.scanner = new ClassScanner();
        this.creatingSnowballs = new HashSet<>();
        this.initializedSnowballs = new HashSet<>();
        this.implementations = new HashMap<>();
        this.logger = new WinterLogger(true);
    }

    public void start() {
        logger.startup();
        logger.log("Starting Winter...");
        logger.log("Scanning package: " + basePackage);
        List<Class<?>> classes =
                scanner.scan(basePackage);

        logger.log(
                "Found " + classes.size() + " classes"
        );
        for (Class<?> clazz : classes) {

            if (!clazz.isAnnotationPresent(Snowball.class)) {
                continue;
            }
            logger.log(
                    "Found Snowball: "
                            + clazz.getName()
            );
            registerImplementations(clazz);

            createSnowball(clazz);
        }
    }

    private Object createSnowball(Class<?> clazz) {
        logger.log(
                "Creating Snowball: "
                        + clazz.getName()
        );
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
                logger.log(
                        "Resolving dependency: "
                                + dependencyType.getName()
                );
                Object dependency =
                        resolveDependency(dependencyType);

                dependencies[i] = dependency;
            }

            Object snowball =
                    constructor.newInstance(dependencies);

            initializeSnowball(snowball);

            Object exposedSnowball = snowball;

            if (clazz.getInterfaces().length > 0) {
                logger.log(
                        "Creating proxy for: "
                                + clazz.getName()
                );
                exposedSnowball =
                        ProxyFactory.createProxy(snowball);
            }

            registry.register(clazz, exposedSnowball);

            return exposedSnowball;

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
        logger.log(
                "Initializing Snowball: "
                        + clazz.getName()
        );
        for (Method method :
                clazz.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(
                    PostSnowball.class)) {
                continue;
            }

            try {

                method.setAccessible(true);
                logger.log(
                        "Executing @PostSnowball: "
                                + method.getName()
                );
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
    private void registerImplementations(Class<?> clazz) {

        for (Class<?> interfaceType :
                clazz.getInterfaces()) {

            implementations.put(
                    interfaceType,
                    clazz
            );
        }
    }
    private Object resolveDependency(Class<?> dependencyType) {
        logger.log(
                "Looking for dependency: "
                        + dependencyType.getName()
        );
        Object existing =
                registry.get(dependencyType);

        if (existing != null) {
            return existing;
        }

        if (dependencyType.isAnnotationPresent(Snowball.class)) {
            return createSnowball(dependencyType);
        }

        Class<?> implementation =
                implementations.get(dependencyType);
        logger.log(
                "Resolved interface "
                        + dependencyType.getName()
                        + " -> "
                        + implementation.getName()
        );
        if (implementation == null) {
            throw new RuntimeException(
                    "No Snowball implementation found for: "
                            + dependencyType.getName()
            );
        }

        return createSnowball(implementation);
    }
}