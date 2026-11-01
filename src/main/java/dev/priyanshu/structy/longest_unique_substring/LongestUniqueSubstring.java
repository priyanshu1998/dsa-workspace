package dev.priyanshu.structy.longest_unique_substring;

import dev.priyanshu.annotation.Structy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Structy(tag = "sliding-window")
public interface LongestUniqueSubstring {
  int longestUniqueSubstring(String s);
}

class NaiveSolution implements LongestUniqueSubstring {

  private boolean isUnique(String s) {
    var set = new HashSet<Character>();
    for (char c : s.toCharArray()) {
      if (set.contains(c)) return false;

      set.add(c);
    }
    return true;
  }

  @Override
  public int longestUniqueSubstring(String s) {
    int maxLength = 0;
    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (isUnique(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        } else {
          break;
        }
      }
    }

    return maxLength;
  }
}

class OptimizedSolution implements LongestUniqueSubstring {

  private void add(Map<Character, Integer> freq, Character c){
    freq.put(c, freq.getOrDefault(c, 0)+1);
  }

  private void remove(Map<Character, Integer> freq, Character c){
    var v = freq.get(c);

    if(v == 1){
      freq.remove(c);
    }else{
      freq.put(c, v-1);
    }
  }


  @Override
  public int longestUniqueSubstring(String s) {
    int maxLength = 0;
    Map<Character, Integer> freq = new HashMap<>();

    int l = 0;

    for(int r=0; r < s.length(); r++) {
      // add leading element
      add(freq, s.charAt(r));

      // remove trailing elements while constraint is violated
      while (freq.get(s.charAt(r)) > 1) {
        remove(freq, s.charAt(l));
        l++;
      }


      maxLength = Math.max(maxLength, r-l+1);
    }

    return maxLength;
  }
}
