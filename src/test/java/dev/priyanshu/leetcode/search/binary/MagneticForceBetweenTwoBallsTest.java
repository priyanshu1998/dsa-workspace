package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MagneticForceBetweenTwoBallsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,4,7}, 3, 3),
                Arguments.of(new int[]{5,4,3,2,1,1000000000}, 2, 999999999)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] position, int m, int expected){
        var solver = new MagneticForceBetweenTwoBallsImpl();
        Assertions.assertEquals(expected, solver.maxDistance(position, m));
    }
}