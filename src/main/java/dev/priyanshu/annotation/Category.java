package dev.priyanshu.annotation;

import dev.priyanshu.enums.Concept;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Category {
  String value() default "";

  Concept[] concepts() default {};
}
