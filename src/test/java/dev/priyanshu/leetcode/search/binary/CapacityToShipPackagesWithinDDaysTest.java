package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CapacityToShipPackagesWithinDDaysTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{1,2,3,4,5,6,7,8,9,10}, 5, 15),
                Arguments.of(new int[]{3,2,2,4,1,4}, 3, 6),
                Arguments.of(new int[]{1,2,3,1,1}, 4, 3)
        );
    }


    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] weights, int days, int expected){
        var solver = new CapacityToShipPackagesWithinDDaysImpl();
        Assertions.assertEquals(expected, solver.shipWithinDays(weights, days));
    }

}