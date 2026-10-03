package dev.priyanshu.striver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindPeakElementTest {

    Stream<int[]> testCases(){
        return Stream.of(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 5, 1},
                new int[]{1, 2, 1, 3, 5, 6, 4},
                new int[]{-2, -1, 3, 4, 5});
    }

    private boolean check(int i, int[] nums){
        if(nums.length == 1) return true;
        if(i == 0){
            return nums[0]>nums[1];
        }else if(i == nums.length-1){
            return nums[nums.length-1]>nums[nums.length-2];
        }else{
            return nums[i-1] < nums[i] && nums[i]>nums[i+1];
        }
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums){
        var solver = new FindPeakElementImpl();
        var i = solver.findPeakElement(nums);
        Assertions.assertTrue(check(i, nums));
    }
}