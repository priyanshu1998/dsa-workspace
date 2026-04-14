package dev.priyanshu.structy.count_substring_exactly_k_distinct;

import java.util.HashMap;
import java.util.Map;

public interface CountExactlyKDistinct {
  long countSubstringExactlyKDistinct(String s, int k);
}

class OptimizedSolution implements CountExactlyKDistinct {

  private void extendLeadingEnd(Map<Character, Integer> map, char c) {
    if (map.containsKey(c)) {
      var v = map.get(c);
      map.put(c, v + 1);
    } else {
      map.put(c, 1);
    }
  }

  private void shrinkTailingEnd(Map<Character, Integer> map, char c) {
    if (map.get(c) == 1) {
      map.remove(c);
    } else {
      var v = map.get(c);
      map.put(c, v - 1);
    }
  }

  @Override
  public long countSubstringExactlyKDistinct(String s, int k) {
    return countAtMostKDistinct(s, k) - countAtMostKDistinct(s, k - 1);
  }

  private long countAtMostKDistinct(String s, int k) {
    long count = 0;

    int l = 0;
    int r = 0;

    var map = new HashMap<Character, Integer>();

    while (r < s.length()) {
      extendLeadingEnd(map, s.charAt(r));
      while (map.size() > k) {
        shrinkTailingEnd(map, s.charAt(l));
        l++;
      }
      count += r - l + 1;
      r++;
    }

    return count;
  }
}
