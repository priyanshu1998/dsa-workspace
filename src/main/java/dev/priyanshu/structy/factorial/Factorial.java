package dev.priyanshu.structy.factorial;

import dev.priyanshu.annotation.Structy;

@Structy(tag = {"recursion"})
public interface Factorial {
  long factorial(int n);
}

class FactorialImpl implements Factorial {

  @Override
  public long factorial(int n) {
    if (n == 0) return 1;

    return n * factorial(n - 1);
  }
}

class FactorialTailRecursionImpl implements Factorial {

  private long factorial(int n, long ret) {
    if (n == 0) return ret;
    return factorial(n - 1, n * ret);
  }

  @Override
  public long factorial(int n) {
    return factorial(n, 1);
  }
}
