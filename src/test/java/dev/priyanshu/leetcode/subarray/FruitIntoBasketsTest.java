package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FruitIntoBasketsTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,1}, 3),
                Arguments.of(new int[]{0,1,2,2}, 3),
                Arguments.of(new int[]{1,2,3,2,2}, 4)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] fruits, int expected){
        var solver = new FruitIntoBasketsImpl();
        Assertions.assertEquals(expected, solver.totalFruit(fruits));
    }

}