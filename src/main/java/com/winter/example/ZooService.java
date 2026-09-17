package com.winter.example;

import com.winter.annotation.Snowball;

@Snowball
public class ZooService {

    private final CatOperations cat;

    public ZooService(CatOperations cat) {
        this.cat = cat;
    }

    public void testCat() {
        cat.meow();
    }
}