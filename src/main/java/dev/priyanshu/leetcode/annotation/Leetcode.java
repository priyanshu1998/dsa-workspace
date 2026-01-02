package dev.priyanshu.leetcode.annotation;

import dev.priyanshu.leetcode.enums.Difficulty;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Leetcode {
  String value() default "";

  String name() default "";

  Difficulty difficulty() default Difficulty.EASY;

  int id() default 0;
}
