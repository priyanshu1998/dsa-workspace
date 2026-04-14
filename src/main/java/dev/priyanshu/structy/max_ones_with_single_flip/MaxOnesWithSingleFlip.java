package dev.priyanshu.structy.max_ones_with_single_flip;

public interface MaxOnesWithSingleFlip {
  int maxOnesWithSingleFlip(String s);
}

class NaiveSolution implements MaxOnesWithSingleFlip {

  private boolean containsAtMostOne0(String s) {
    long ones = s.chars().filter(c -> c == '1').count();
    long zeros = s.length() - ones;

    return zeros == 0 || zeros == 1;
  }

  private boolean check(String substring) {
    return containsAtMostOne0(substring);
  }

  @Override
  public int maxOnesWithSingleFlip(String s) {
    int maxLength = 0;

    for (int i = 0; i < s.length(); i++) {
      for (int j = i + 1; j <= s.length(); j++) {
        if (check(s.substring(i, j))) {
          maxLength = Math.max(maxLength, j - i);
        } else {
          break;
        }
      }
    }

    return maxLength;
  }
}

class OptimizedSolution implements MaxOnesWithSingleFlip {

  int count0 = 0;
  int count1 = 0;

  void addLeadingBit(char c) {
    if (c == '0') {
      count0++;
    } else {
      count1++;
    }
  }

  void removeTrailingBit(char c) {
    if (c == '0') {
      count0--;
    } else {
      count1--;
    }
  }

  @Override
  public int maxOnesWithSingleFlip(String s) {
    int maxLength = 0;

    int l = 0;
    int r = 0;

    while (r < s.length()) {
      addLeadingBit(s.charAt(r));

      while (count0 == 2) {
        removeTrailingBit(s.charAt(l));
        l++;
      }

      maxLength = Math.max(maxLength, r - l + 1);
      r++;
    }

    return maxLength;
  }
}
