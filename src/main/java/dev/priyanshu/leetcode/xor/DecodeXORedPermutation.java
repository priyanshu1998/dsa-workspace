package dev.priyanshu.leetcode.xor;

import dev.priyanshu.annotation.Category;
import dev.priyanshu.annotation.Leetcode;
import dev.priyanshu.enums.Concept;

@Leetcode(id = 1734, name = "decode-xored-permutation")
@Category(concepts = {Concept.XOR_ALL, Concept.XOR_PERMUTATION_GROUP})
public interface DecodeXORedPermutation {
  int[] decode(int[] encoded);
}

class ResolveUsingXOR implements DecodeXORedPermutation {
  private int xorOfFirstN(int n) {
    int acc = 0;
    for (int i = 1; i <= n; i++) {
      acc ^= i;
    }
    return acc;
  }

  private int getFirstElement(int[] encoded) {
    int xor = xorOfFirstN(encoded.length + 1);
    for (int i = 1; i < encoded.length; i += 2) {
      xor = xor ^ encoded[i];
    }

    return xor;
  }

  public int[] decode(int[] encoded) {
    int n = encoded.length + 1;

    int[] x = new int[n];
    x[0] = getFirstElement(encoded);

    for (int i = 1; i < n; i++) {
      x[i] = x[i - 1] ^ encoded[i - 1];
    }

    return x;
  }
}
