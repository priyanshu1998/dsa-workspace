package dev.priyanshu.leetcode.annotation;

import dev.priyanshu.leetcode.enums.Concept;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Category {
  String value() default "";

  Concept[] concepts() default {};
}
