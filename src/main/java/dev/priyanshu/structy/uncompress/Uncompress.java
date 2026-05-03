package dev.priyanshu.structy.uncompress;

public interface Uncompress {
  String uncompress(String s);
}

class UncompressImpl implements Uncompress {

  public static final char NULL = '\0';

  record Window(int l, int r) {}

  private boolean isDigit(Character c) {
    return (c != NULL && Character.isDigit(c));
  }

  private boolean isAlphabetic(Character c) {
    return (c != NULL && Character.isAlphabetic(c));
  }

  private Character charAt(int i) {
    if (i == s.length()) {
      return NULL;
    }
    return s.charAt(i);
  }

  private Window getDigitWindow(int l, int r) {
    while (isDigit(charAt(r))) {
      r++;
    }
    return new Window(l, r);
  }

  private Window getCharWindow(int l, int r) {
    while (isAlphabetic(charAt(r))) {
      r++;
    }
    return new Window(l, r);
  }

  private void append(StringBuilder sb, String ss, int count) {
    sb.append(ss.repeat(Math.max(0, count)));
  }

  String s;

  @Override
  public String uncompress(String s) {
    this.s = s;

    StringBuilder sb = new StringBuilder();
    int l = 0;
    int r = 1;

    while (l < s.length()) {
      var digitWindow = getDigitWindow(l, r);
      int count = Integer.parseInt(s.substring(digitWindow.l(), digitWindow.r()));
      l = digitWindow.r();
      r = l + 1;

      var charWindow = getCharWindow(l, r);
      var ss = s.substring(charWindow.l(), charWindow.r());
      l = charWindow.r();
      r = l + 1;

      append(sb, ss, count);
    }

    return sb.toString();
  }
}
