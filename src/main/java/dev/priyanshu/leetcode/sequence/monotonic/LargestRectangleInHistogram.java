package dev.priyanshu.leetcode.sequence.monotonic;

import dev.priyanshu.annotation.Leetcode;

import java.util.Stack;

@Leetcode(id=84, name="largest-rectangle-in-histogram")
public interface LargestRectangleInHistogram {
    int largestRectangleArea(int[] heights);
}

class LargestRectangleInHistogramImpl implements LargestRectangleInHistogram {
    record HeightRecord(int i, int h){};

    Stack<HeightRecord> stack = new Stack<>();
    int max = 0;

    void push(HeightRecord v){
        int start = v.i;
        while (!stack.isEmpty() && stack.peek().h > v.h) {
            var top = stack.pop();
            max = Math.max(max, top.h * (v.i - top.i));
            start = top.i;   // propagate left boundary
        }
        stack.push(new HeightRecord(start, v.h));
    }

    @Override
    public int largestRectangleArea(int[] heights) {
        for(int i=0; i<heights.length; i++){
            push(new HeightRecord(i, heights[i]));
        }
        push(new HeightRecord(heights.length, 0));

        return max;
    }
}
