package dev.priyanshu.leetcode.geometry;

import dev.priyanshu.annotation.Leetcode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Leetcode(id = 2975, name = "maximum-square-area-by-removing-fences-from-a-field")
public interface MaximumSquareAreaByRemovingFencesFromAField {
  int maximizeSquareArea(int m, int n, int[] hFences, int[] vFences);
}

class MaximumSquareAreaByRemovingFencesFromAFieldImpl
    implements MaximumSquareAreaByRemovingFencesFromAField {

  @Override
  public int maximizeSquareArea(int m, int n, int[] hFences, int[] vFences) {
    List<Integer> verticals = new ArrayList<>();
    verticals.add(1);
    verticals.add(n);
    verticals.addAll(Arrays.stream(vFences).boxed().toList());

    List<Integer> horizontals = new ArrayList<>();
    horizontals.add(1);
    horizontals.add(m);
    horizontals.addAll(Arrays.stream(hFences).boxed().toList());

    Set<Integer> lengths = new HashSet<>();

    for (int i = 0; i < horizontals.size(); i++) {
      for (int j = i + 1; j < horizontals.size(); j++) {
        var topBoundary = horizontals.get(i);
        var bottomBoundary = horizontals.get(j);

        lengths.add(Math.abs(bottomBoundary - topBoundary));
      }
    }

    long M = 0;
    for (int i = 0; i < verticals.size(); i++) {
      for (int j = i + 1; j < verticals.size(); j++) {
        var leftBoundary = verticals.get(i);
        var rightBoundary = verticals.get(j);

        var breadth = Math.abs(rightBoundary - leftBoundary);

        if (lengths.contains(breadth)) {
          M = Math.max(M, breadth);
        }
      }
    }

    return (M != 0) ? Math.toIntExact((M * M) % 1000_000_007) : -1;
  }
}
