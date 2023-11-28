package com.test.game;

public class MathFunctions {
    public static Float lerp(Float a, Float b, Float t) {
        return a + (b - a) * t;
    }

    public static Float clamp(Float value, Float min, Float max) {
        return Math.max(min, Math.min(max, value));
    }
}
