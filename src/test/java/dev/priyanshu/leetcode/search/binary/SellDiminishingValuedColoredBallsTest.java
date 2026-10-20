package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SellDiminishingValuedColoredBallsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{2, 5}, 4, 14),
                Arguments.of(new int[]{3, 5}, 6, 19),
                Arguments.of(new int[]{680754224,895956841,524658639,3161416,992716630,7365440,
                        582714485,422256708,332815744,269407767}, 707568720, 6150955),
                Arguments.of(new int[]{4420049,199487178,553327648,737784415,296875712,820580191,
                        644001538,357887155,608015365,232020440,296469719,264703910,980408347},
                        14879651, 940003805)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] inventory, int orders, int expected){
        var solver = new SellDiminishingValuedColoredBallsImpl();
        Assertions.assertEquals(expected, solver.maxProfit(inventory, orders));
    }
}