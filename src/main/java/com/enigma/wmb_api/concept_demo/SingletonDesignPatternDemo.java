package com.enigma.wmb_api.concept_demo;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SingletonDesignPatternDemo {
    static SingletonDesignPatternDemo instance = null;

    public static SingletonDesignPatternDemo getInstance() {
        if (instance == null) {
            instance = new SingletonDesignPatternDemo(); // dengan seperti ini, instance atau object yang terbuat hanya sekali saja, walaupun dipakai diberbagai
            // berbagai object lainnya
            return instance;
        }
        return instance;
    }
}
