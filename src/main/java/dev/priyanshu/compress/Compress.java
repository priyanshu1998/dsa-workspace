package dev.priyanshu.compress;

public interface Compress {
  String compress(String s);
}

class CompressImpl implements Compress {

  String s;

  private char charAt(int i) {
    if (i == s.length()) {
      return '\0';
    }
    return s.charAt(i);
  }

  void append(StringBuilder sb, int l, int r) {
    if (charAt(l) == '\0') return;

    if (r - l != 1) sb.append(r - l);
    sb.append(charAt(l));
  }

  @Override
  public String compress(String s) {
    this.s = s;
    var sb = new StringBuilder();

    int l = 0;
    int r = 1;

    while (r <= s.length()) {
      while (charAt(l) == charAt(r)) {
        r++;
      }

      append(sb, l, r);
      l = r;
      r = l + 1;
    }

    return sb.toString();
  }
}
