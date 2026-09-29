package dev.priyanshu.annotation;


public @interface Striver {
    String[] groups() default {};
    String category();
    String path();
}
