package dev.priyanshu.structy.is_subsequence;

public interface IsSubsequence {
  boolean isSubsequence(String s, String t);
}

class IsSubsequenceImpl implements IsSubsequence {

  @Override
  public boolean isSubsequence(String s, String t) {
    int i = 0;
    int j = 0;

    while (i < s.length() && j < t.length()) {
      if (t.charAt(j) == s.charAt(i)) {
        i++;
        j++;
      } else {
        j++;
      }
    }

    return i == s.length();
  }
}
