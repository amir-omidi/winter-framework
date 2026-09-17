package com.winter.example.services;

import com.winter.annotation.Snowball;

@Snowball
public class DogService {

    public void bark() {
        System.out.println("Woof!");
    }
}