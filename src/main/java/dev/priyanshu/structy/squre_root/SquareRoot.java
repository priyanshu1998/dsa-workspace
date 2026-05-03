package dev.priyanshu.structy.squre_root;

public interface SquareRoot {
  long sqRoot(long num);
}

class SquareRootImpl implements SquareRoot {

  @Override
  public long sqRoot(long num) {
    long l = 0;
    long r = num;

    while (l <= r) {
      long mid = (l + r) / 2;
      long midSq = mid * mid;
      if (mid * mid == num) {
        return mid;
      } else if (midSq < num) {
        l = mid + 1;
      } else {
        r = mid - 1;
      }
    }

    return l - 1;
  }
}
