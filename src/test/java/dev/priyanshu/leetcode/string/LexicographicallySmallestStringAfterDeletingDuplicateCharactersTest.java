package dev.priyanshu.leetcode.string;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LexicographicallySmallestStringAfterDeletingDuplicateCharactersTest {

  Stream<Arguments> testCases() {
    return Stream.of(Arguments.of("aaccb", "aacb"), Arguments.of("z", "z"));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  @Timeout(value = 1, unit = TimeUnit.SECONDS)
  void test(String s, String expected) {
    var solver = new LexicographicallySmallestStringAfterDeletingDuplicateCharactersImpl();
    Assertions.assertEquals(expected, solver.lexSmallestAfterDeletion(s));
  }

  @Test
  @Timeout(value = 10, unit = TimeUnit.SECONDS)
  void testNeedsDP() {
    var solver = new LexicographicallySmallestStringAfterDeletingDuplicateCharactersImpl();
    Assertions.assertEquals("aaax", solver.lexSmallestAfterDeletion("xaaaxaaaaaa"));
  }

  @Test
  @Timeout(value = 1, unit = TimeUnit.SECONDS)
  void testNeedsOptimizedAlgorithm() {
    var solver = new LexicographicallySmallestStringAfterDeletingDuplicateCharactersImpl();
    Assertions.assertEquals("?", solver.lexSmallestAfterDeletion("qehhffhhyrvrvsryyhyqqfsese"));
  }
}
