package dev.priyanshu.structy.sum_of_lengths;

import dev.priyanshu.annotation.Structy;
import java.util.Arrays;

@Structy(tag = {"recursion"})
public interface SumOfLengths {
  int getLength(String[] stringArray);
}

class SumOfLengthImpl implements SumOfLengths {

  @Override
  public int getLength(String[] stringArray) {
    if (stringArray.length == 0) {
      return 0;
    }

    return stringArray[0].length()
        + getLength(Arrays.copyOfRange(stringArray, 1, stringArray.length));
  }
}

class SumOfLengthTailRecursion implements SumOfLengths {

  private int getLength(String[] stringArray, int i, int length) {
    if (i == stringArray.length) {
      return length;
    }

    return getLength(stringArray, i + 1, length + stringArray[i].length());
  }

  @Override
  public int getLength(String[] stringArray) {
    return getLength(stringArray, 0, 0);
  }
}
