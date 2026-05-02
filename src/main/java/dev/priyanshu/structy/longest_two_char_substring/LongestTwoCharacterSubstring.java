package dev.priyanshu.structy.longest_two_char_substring;

import dev.priyanshu.annotation.Structy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Structy(tag = "sliding-window")
public interface LongestTwoCharacterSubstring {
  int longestTwoCharSubstring(String s);
}

class NaiveSolution implements LongestTwoCharacterSubstring {

  private boolean check(String s) {
    return s.chars().distinct().count() == 2;
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    int maxLength = 0;
    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (check(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        }
      }
    }
    return maxLength;
  }
}

class IntermediateOptimization implements LongestTwoCharacterSubstring {

  private boolean substringContainsAtMostTwoDistinctCharacter(String s) {
    var set = new HashSet<Character>();
    for (char c : s.toCharArray()) {
      if (!set.contains(c) && set.size() == 2) {
        return false;
      }
      set.add(c);
    }
    return true;
  }

  boolean substringContainsOneDistinctCharacter(String s) {
    return s.chars().distinct().count() == 1;
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    if (substringContainsOneDistinctCharacter(s)) return 0;

    int maxLength = 0;
    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (substringContainsAtMostTwoDistinctCharacter(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        } else {
          break;
        }
      }
    }

    return maxLength;
  }
}

class OptimizedSolution implements LongestTwoCharacterSubstring {

  private void put(Map<Character, Integer> map, char c) {
    map.put(c, map.getOrDefault(c, 0) + 1);
  }

  private void remove(Map<Character, Integer> map, char c) {
    map.put(c, map.get(c) - 1);
    if (map.get(c).equals(0)) {
      map.remove(c);
    }
  }

  @Override
  public int longestTwoCharSubstring(String s) {
    int maxLength = 0;
    var map = new HashMap<Character, Integer>();

    int l = 0;
    int r = 0;

    while (r < s.length()) {
      put(map, s.charAt(r));

      while (map.size() > 2) {
        remove(map, s.charAt(l));
        l++;
      }

      if (map.size() == 2) {
        maxLength = Math.max(maxLength, r - l + 1);
      }

      r++;
    }

    return maxLength;
  }
}
