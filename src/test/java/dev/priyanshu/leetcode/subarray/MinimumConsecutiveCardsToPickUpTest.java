package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimumConsecutiveCardsToPickUpTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{3,4,2,3,4,7}, 4),
                Arguments.of(new int[]{1,0,5,3}, -1),
                Arguments.of(new int[]{70,37,70,41,1,62,71,49,38,50,29,76,29,41,22,66,88,18,85,53}, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] cards, int expected){
        var solver = new MinimumConsecutiveCardsToPickUpImpl();
        Assertions.assertEquals(expected, solver.minimumCardPickup(cards));
    }
}