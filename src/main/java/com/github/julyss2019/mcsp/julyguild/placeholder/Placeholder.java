package com.github.julyss2019.mcsp.julyguild.placeholder;

public class Placeholder {
    private String key;
    private Object value;

    Placeholder(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
