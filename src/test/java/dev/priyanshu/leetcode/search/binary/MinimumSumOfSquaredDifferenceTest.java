package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumSumOfSquaredDifferenceTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,4}, new int[]{2,10,20,19}, 0, 0, 579),
                Arguments.of(new int[]{1,4,10,12}, new int[]{5,8,6,9}, 1, 1, 43),
                Arguments.of(new int[]{11,12,13,14,15}, new int[]{13,16,16,12,14}, 3, 6, 3)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums1, int[] nums2, int k1, int k2, long expected){
        var solver = new MinimumSumOfSquaredDifferenceImpl();
        Assertions.assertEquals(expected, solver.minSumSquareDiff(nums1, nums2, k1, k2));
    }

    @Test
    void largeInputFromFile() throws IOException {
        var lines = Files.readAllLines(Path.of(
                "src/main/resources/dev/priyanshu/leetcode/search/binary/MinimumSumOfSquaredDifference/input.txt"));

        int[] nums1 = parseArray(lines.get(0));
        int[] nums2 = parseArray(lines.get(1));
        int k1 = Integer.parseInt(lines.get(2));
        int k2 = Integer.parseInt(lines.get(3));
        long expected = Long.parseLong(lines.get(4));

        var solver = new MinimumSumOfSquaredDifferenceImpl();
        Assertions.assertEquals(expected, solver.minSumSquareDiff(nums1, nums2, k1, k2));
    }

    private int[] parseArray(String line){
        var s = line.substring(1, line.length() - 1); // remove [ and ]
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
    }

}