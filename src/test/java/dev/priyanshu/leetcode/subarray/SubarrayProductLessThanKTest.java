package dev.priyanshu.leetcode.subarray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubarrayProductLessThanKTest {

    Stream<Arguments> testCases(){
        return Stream.of(
                Arguments.of(new int[]{10,5,2,6}, 100, 8),
                Arguments.of(new int[]{1,2,3}, 0, 0),
                Arguments.of(new int[]{323, 578, 620, 493, 260, 690, 407, 867, 321, 148, 509, 601, 216, 158,
                        491, 267, 67, 389, 321, 137, 751, 254, 729, 416, 437, 930, 101, 903, 902, 94, 13, 745, 479, 125,
                        839, 433, 4, 954, 416, 732, 691, 117, 698, 993, 812, 632, 749, 282, 864, 76, 944, 958, 214, 213,
                        406, 801, 864, 672, 511, 522, 822, 524, 889, 329, 837, 901, 993, 655, 709, 17, 203, 151, 246, 272,
                        125, 865, 869, 25, 84, 747, 677, 741, 522, 808, 31, 117, 173, 75, 206, 689, 314, 570, 180, 949,
                        408, 463, 117, 519, 687, 50, 766, 955, 457, 71, 707, 292, 94, 252, 860, 105, 378, 665, 116, 234,
                        881, 149, 519, 281, 323, 158, 73, 903, 739, 476, 320, 222, 45, 33, 410, 503, 850, 768, 722, 301,
                        424, 679, 34, 643, 93, 413, 368, 468, 580, 169, 41, 615, 766, 566, 927, 944, 897, 312, 400, 478,
                        103, 430, 964, 680, 608, 411, 392, 727, 582, 423, 602, 552, 29, 853, 586, 710, 941, 798, 530, 829,
                        393, 975, 940, 741, 291, 103, 573, 848, 330, 995, 265, 366, 428, 468, 501, 472, 420, 162, 326, 728,
                        717, 300, 502, 435, 729, 766, 955, 703, 590, 485, 501, 301, 512, 639, 799, 498, 471, 631, 103, 917,
                        870, 900, 216, 63, 573, 605, 626, 871, 723, 572, 440, 858, 350, 930, 418, 483, 811, 240, 376, 477,
                        406, 871, 714, 13, 272, 102, 995, 256, 146, 892, 584, 10, 566, 136, 973, 578, 208, 872, 991, 8,
                        600, 193, 22, 385, 657, 132, 710, 997, 895, 487, 686, 675, 706, 235, 555, 282, 291, 55, 939, 168,
                        113, 209, 679, 358, 395, 933, 402, 24, 724, 946, 897}, 259, 71)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(int[] nums, int k, int expected){
        var solver = new SubarrayProductLessThanKImpl();
        Assertions.assertEquals(expected, solver.numSubarrayProductLessThanK(nums, k));
    }
}