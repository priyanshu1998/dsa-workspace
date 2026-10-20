package dev.priyanshu.cses.sliding_window;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlidingWindowDistinctValuesTest {

    public static final String INPUT_TXT =
            "/dev/priyanshu/cses/sliding_window/SlidingWindowDistinctValues/input1.txt";

    public static final String OUTPUT_TXT =
            "/dev/priyanshu/cses/sliding_window/SlidingWindowDistinctValues/output1.txt";
    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(8, 3, new int[]{1,2,3,2,5,2,2,2}, new int[]{3, 2, 3, 2, 2, 1})
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int n, int k, int[] nums, int [] expected){
        var solver = new SlidingWindowDistinctValuesImpl();
        Assertions.assertArrayEquals(expected, solver.windowDistincts(n, k, nums));
    }

    @Test
    void invokerTest(){
        try(var inputStream = getClass().getResourceAsStream(INPUT_TXT);
         var expectationStream = getClass().getResourceAsStream(OUTPUT_TXT)){
            var expectationScanner = new Scanner(expectationStream);

            var result = SlidingWindowDistinctValues.invoker(inputStream);
            for(int i=0; i<result.length; i++){
                Assertions.assertEquals(expectationScanner.nextInt(), result[i]);
            }
            Assertions.assertFalse(expectationScanner.hasNext());

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}