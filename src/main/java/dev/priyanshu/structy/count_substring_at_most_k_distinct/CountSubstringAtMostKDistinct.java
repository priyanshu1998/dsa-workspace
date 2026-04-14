package dev.priyanshu.structy.count_substring_at_most_k_distinct;

import java.util.HashMap;
import java.util.Map;

public interface CountSubstringAtMostKDistinct {
  long countSubstringAtMostKDistinct(String s, int k);
}

class NaiveSolution implements CountSubstringAtMostKDistinct {

  private boolean check(String s, int k) {
    return s.chars().distinct().count() <= k;
  }

  @Override
  public long countSubstringAtMostKDistinct(String s, int k) {
    long count = 0;

    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (check(s.substring(i, j), k)) {
          count++;
        } else {
          break;
        }
      }
    }

    return count;
  }
}

class OptimizedSolution implements CountSubstringAtMostKDistinct {

  void addToLeadingEnd(Map<Character, Integer> map, char c) {
    if (map.containsKey(c)) {
      int v = map.get(c);
      map.put(c, v + 1);
    } else {
      map.put(c, 1);
    }
  }

  void removeFromTailingEnd(Map<Character, Integer> map, char c) {
    int v = map.get(c);
    if (v == 1) {
      map.remove(c);
    } else {
      map.put(c, v - 1);
    }
  }

  @Override
  public long countSubstringAtMostKDistinct(String s, int k) {
    long count = 0;

    int l = 0;
    int r = 0;

    var map = new HashMap<Character, Integer>();

    while (r < s.length()) {
      addToLeadingEnd(map, s.charAt(r));
      while (map.size() > k) {
        removeFromTailingEnd(map, s.charAt(l));
        l++;
      }
      count += (r - l + 1);

      r++;
    }

    return count;
  }
}
