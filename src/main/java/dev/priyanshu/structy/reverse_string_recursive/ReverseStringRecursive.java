package dev.priyanshu.structy.reverse_string_recursive;

import dev.priyanshu.annotation.Structy;

@Structy(tag = {"recursion"})
public interface ReverseStringRecursive {
  String reverseString(String s);
}

class ReverseStringTailRecursion implements ReverseStringRecursive {

  private String reverseString(String s, int i, StringBuilder sb) {
    if (i == -1) {
      return sb.toString();
    }

    sb.append(s.charAt(i));
    return reverseString(s, i - 1, sb);
  }

  @Override
  public String reverseString(String s) {
    var sb = new StringBuilder();
    return reverseString(s, s.length() - 1, sb);
  }
}

class ReverseStringRecursiveImpl implements ReverseStringRecursive {

  @Override
  public String reverseString(String s) {
    if (s.isEmpty()) return "";
    char c = s.charAt(0);
    return reverseString(s.substring(1)) + c;
  }
}
