package dev.priyanshu.structy.is_palindrome;

public interface IsPalindrome {
  boolean isPalindrome(String s);
}

class IsPalindromeImpl implements IsPalindrome {

  @Override
  public boolean isPalindrome(String s) {
    int l = 0;
    int r = s.length() - 1;

    while (l < r) {
      if (s.charAt(l) != s.charAt(r)) {
        return false;
      }
      l += 1;
      r -= 1;
    }

    return true;
  }
}
