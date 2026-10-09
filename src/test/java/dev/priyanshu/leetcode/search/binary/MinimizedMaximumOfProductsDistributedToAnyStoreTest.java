package dev.priyanshu.leetcode.search.binary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinimizedMaximumOfProductsDistributedToAnyStoreTest {

    Stream<Arguments> testCases(){
        return Stream.of(
               Arguments.of(6, new int[]{11, 6}, 3),
                Arguments.of(7,new int[] {15,10,10}, 5),
                Arguments.of(1, new int[]{100000}, 100000),
                Arguments.of(1, new int[]{1}, 1),
                Arguments.of(1000000, new int[]{2}, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int[] capacities, int expectation){
        var solver = new MinimizedMaximumOfProductsDistributedToAnyStoreImpl();
        Assertions.assertEquals(expectation, solver.minimizedMaximum(n, capacities));
    }
}