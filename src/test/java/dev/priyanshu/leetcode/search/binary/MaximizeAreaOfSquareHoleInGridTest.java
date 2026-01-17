package dev.priyanshu.leetcode.search.binary;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaximizeAreaOfSquareHoleInGridTest {

  Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(2, 1, new int[] {2, 3}, new int[] {2}, 4),
        Arguments.of(1, 1, new int[] {2}, new int[] {2}, 4),
        Arguments.of(2, 3, new int[] {2, 3}, new int[] {2, 4}, 4),
        Arguments.of(
            1000000000,
            1000000000,
            new int[] {
              2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
              25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
              46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66,
              67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87,
              88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101
            },
            new int[] {
              100002, 100003, 100004, 100005, 100006, 100007, 100008, 100009, 100010, 100011,
              100012, 100013, 100014, 100015, 100016, 100017, 100018, 100019, 100020, 100021,
              100022, 100023, 100024, 100025, 100026, 100027, 100028, 100029, 100030, 100031,
              100032, 100033, 100034, 100035, 100036, 100037, 100038, 100039, 100040, 100041,
              100042, 100043, 100044, 100045, 100046, 100047, 100048, 100049, 100050, 100051,
              100052, 100053, 100054, 100055, 100056, 100057, 100058, 100059, 100060, 100061,
              100062, 100063, 100064, 100065, 100066, 100067, 100068, 100069, 100070, 100071,
              100072, 100073, 100074, 100075, 100076, 100077, 100078, 100079, 100080, 100081,
              100082, 100083, 100084, 100085, 100086, 100087, 100088, 100089, 100090, 100091,
              100092, 100093, 100094, 100095, 100096, 100097, 100098, 100099, 100100, 100101
            },
            10201));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void test(int n, int m, int[] hBars, int[] vBars, int expected) {

    var solver = new MaximizeAreaOfSquareHoleInGridImpl();
    Assertions.assertEquals(expected, solver.maximizeSquareHoleArea(n, m, hBars, vBars));
  }
}
