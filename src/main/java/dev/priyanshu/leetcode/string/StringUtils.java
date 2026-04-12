package dev.priyanshu.leetcode.string;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringUtils {
  public static Map<Character, Long> characterCount(String s) {
    return s.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }
}
