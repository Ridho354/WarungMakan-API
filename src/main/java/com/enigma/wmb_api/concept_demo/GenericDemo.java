package com.enigma.wmb_api.concept_demo;

public class GenericDemo<X> {
    private X value;

    public GenericDemo(X value) {
        this.value = value;
    }

    public X getValue() {
        return value;
    }
}
