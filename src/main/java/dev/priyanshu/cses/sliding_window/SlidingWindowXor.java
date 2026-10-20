package dev.priyanshu.cses.sliding_window;

import java.io.InputStream;
import java.util.Scanner;

public interface SlidingWindowXor {
    int windowXors(int n, int k, int x, int a, int b, int c);

    private static int invoker(InputStream inputStream){
        var scanner = new Scanner(inputStream);

        int n = scanner.nextInt();
        int k = scanner.nextInt();
        int x = scanner.nextInt();
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();

        var solver = new SlidingWindowXorImpl();
        return Math.toIntExact(solver.windowXors(n, k, x, a, b, c));
    }

    static void main(String[] args) {
        System.out.println(invoker(System.in));
    }
}

class SlidingWindowXorImpl implements SlidingWindowXor {
    int a;
    int b;
    int c;

    private static class WindowTuple{
        int start;
        int next;
        int xor;

        private WindowTuple(int start, int next, int xor){
            this.start = start;
            this.next = next;
            this.xor = xor;
        }
    }

    private WindowTuple initialize(int n, int k, int x){
        long xi = x;

        long xor = 0;
        for(int i=0; i<k; i++){
//            System.out.printf("xi = %d\n", xi);
            xor ^= xi;
            xi = next(xi);
        }
        return new WindowTuple(x, Math.toIntExact(xi), Math.toIntExact(xor));
    }

    private WindowTuple updateWindow(WindowTuple tuple){
        int start = tuple.start;
        int curr = tuple.next;
        int xor = tuple.xor;

        int newXor = xor ^ start ^ curr;
//        System.out.printf("xi = %d\n", curr);

        return new WindowTuple(next(start), next(curr), newXor);
    }

    private int next(long xi){
        var v = Math.toIntExact(((a*xi)%c + b%c)%c);
        return v;

    }

    @Override
    public int windowXors(int n, int k, int x, int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;

        var tuple = initialize(n, k, x);
        var result = tuple.xor;

        int r = k;
        while(r < n){
            tuple = updateWindow(tuple);
            result ^= tuple.xor;
            r++;
        }

        return result;
    }
}

