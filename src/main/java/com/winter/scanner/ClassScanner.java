package com.winter.scanner;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public List<Class<?>> scan(String packageName) {

        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');

        try {
            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();

            URL resource =
                    classLoader.getResource(path);

            if (resource == null) {
                return classes;
            }

            File directory =
                    new File(resource.toURI());

            scanDirectory(
                    directory,
                    packageName,
                    classes
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not scan package: " + packageName,
                    e
            );
        }

        return classes;
    }

    private void scanDirectory(
            File directory,
            String packageName,
            List<Class<?>> classes) {

        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {

                String subPackage =
                        packageName + "." + file.getName();

                scanDirectory(
                        file,
                        subPackage,
                        classes
                );

                continue;
            }

            if (!file.getName().endsWith(".class")) {
                continue;
            }

            String className =
                    packageName + "."
                            + file.getName()
                            .replace(".class", "");

            try {
                classes.add(
                        Class.forName(className)
                );

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                        "Could not load class: " + className,
                        e
                );
            }
        }
    }
}