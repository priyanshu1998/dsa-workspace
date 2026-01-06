package dev.priyanshu.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Leetcode {
  String value() default "";

  String name() default "";

  int id() default 0;
}
