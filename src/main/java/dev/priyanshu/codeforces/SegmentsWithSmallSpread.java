package dev.priyanshu.codeforces;

public interface SegmentsWithSmallSpread {
    //  good if the difference between the maximum and minimum elements on this segment is at most k
    //  Your task is to find the number of different good segments

    long countGoodSegment(int n, long[] nums, long k);
}
