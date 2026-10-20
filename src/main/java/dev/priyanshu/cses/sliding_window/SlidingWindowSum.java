package dev.priyanshu.cses.sliding_window;

import java.io.InputStream;
import java.util.Scanner;

public interface SlidingWindowSum {
    long windowSums(int n, int k, int x, int a, int b, int c);

    static void invoker(InputStream in){
        Scanner scanner = new Scanner(in);

        int n = scanner.nextInt();
        int k = scanner.nextInt();

        int x = scanner.nextInt();
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();

        var solver = new SlidingWindowSumImpl();
        System.out.println(solver.windowSums(n, k, x, a, b, c));
    }

    static void main(String[] args){
        invoker(System.in);
    }
}

class SlidingWindowSumImpl implements SlidingWindowSum {

//    private record WindowTuple(int first, int last, long sum) {}
    private static final class WindowTuple {
        private final int first;
        private final int last;
        private final long sum;

        private WindowTuple(int first, int last, long sum) {
            this.first = first;
            this.last = last;
            this.sum = sum;
        }

        @Override
        public String toString() {
            return "WindowTuple[first=" + first + ", last=" + last + ", sum=" + sum + "]";
        }
    }

    private WindowTuple sum(int k, int x, int a, int b, int c){
        long xi = x;
        long sum = x;
        System.out.printf("xi = %d\n", xi);

        var tuple = new WindowTuple(x, x, sum);

        for(int i=1; i<k; i++){
            xi = ((a*xi)%c + b%c)%c;
            System.out.printf("xi = %d\n", xi);

            if(xi < 0) xi += c;
            sum += xi;
            tuple = new WindowTuple(x, Math.toIntExact(xi), sum);
        }

//        System.out.printf("window = %s\n", tuple);
        return tuple;
    }

    private WindowTuple updateSum(WindowTuple prevWindow, int a, int b, int c){
        int newFirst = next(prevWindow.first, a, b, c);
        int newLast = next(prevWindow.last, a, b, c);
        long newSum = prevWindow.sum - prevWindow.first + newLast;

        System.out.printf("xi = %d\n", newLast);
        var tuple = new WindowTuple(newFirst, newLast, newSum);
//        System.out.printf("window = %s\n", tuple);

        return tuple;

    }

    private int next(long xi, int a, int b, int c){
        return Math.toIntExact(((a*xi)%c + b%c)%c);
    }


    @Override
    public long windowSums(int n, int k, int x, int a, int b, int c) {
        var windowTuple = sum(k, x, a, b, c);

        int r = k;
        var res = windowTuple.sum;
//        System.out.printf("sum = %d\n", res);

        while(r < n){
            windowTuple = updateSum(windowTuple, a, b, c);
//            System.out.printf("sum = %d\n", windowTuple.sum);

            res ^= windowTuple.sum;
            r++;
        }

        return res;
    }
}
