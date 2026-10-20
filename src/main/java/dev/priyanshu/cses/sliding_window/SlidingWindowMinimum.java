package dev.priyanshu.cses.sliding_window;


import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public interface SlidingWindowMinimum {
    long windowMinimums(int n, int k, int x, int a, int b, int c);

    static void invoker(InputStream in){
        Scanner scanner = new Scanner(in);

        int n = scanner.nextInt();
        int k = scanner.nextInt();

        int x = scanner.nextInt();
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();

        var solver = new SlidingWindowMinimumImpl();
        System.out.println(solver.windowMinimums(n, k, x, a, b, c));
    }

    static void main(String[] args){
        invoker(System.in);
    }
}

class SlidingWindowMinimumImpl implements SlidingWindowMinimum {

    int a;
    int b;
    int c;

    private static class EnumeratedArrayItem {
        int i;
        int xi;

        private EnumeratedArrayItem(int i, int xi){
            this.i = i;
            this.xi = xi;
        }

        @Override
        public String toString() {
            return "EnumeratedArrayItem{" +
                    "i=" + i +
                    ", xi=" + xi +
                    '}';
        }
    }

    private static class WindowTuple {
        int min;
        EnumeratedArrayItem next;

        private WindowTuple(int min, EnumeratedArrayItem next){
            this.min = min;
            this.next = next;
        }
    }


    private EnumeratedArrayItem next(long xi, int i){
        return new EnumeratedArrayItem(i+1, Math.toIntExact(((a*xi)%c + b%c)%c));
    }

    private WindowTuple buildInitialWindow(int n, int k, int x, Deque<EnumeratedArrayItem> deque){
        var item = new EnumeratedArrayItem(0, x);

        for(int i=0; i<k; i++){
            addNew(n, k, item, deque);
            item = next(item.xi, item.i);
        }

        return new WindowTuple(deque.getFirst().xi, item);
    }

    private void addNew(int n, int k, EnumeratedArrayItem item, Deque<EnumeratedArrayItem> deque){
        while(!deque.isEmpty() && deque.getLast().xi > item.xi){
            deque.removeLast();
        }
        deque.addLast(item);
    }

    private void removeFront(Deque<EnumeratedArrayItem> deque, int l){
        while(!deque.isEmpty() && deque.getFirst().i <= l){
            deque.removeFirst();
        }
    }

    private int windowMinimum(Deque<EnumeratedArrayItem> deque, int l, int r){
        var front = deque.getFirst();
        while(deque.getFirst().i < l){
            deque.removeFirst();
            front = deque.getFirst();
        }
//        System.out.println(deque);
        return front.xi;
    }

    @Override
    public long windowMinimums(int n, int k, int x, int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;

        Deque<EnumeratedArrayItem> deque = new ArrayDeque<>();
        var windowContext = buildInitialWindow(n, k, x, deque);

        var result = windowContext.min;
        var item = windowContext.next;

        int l = 0;
        int r = k;

        while(r < n){
            removeFront(deque, l); l++;
            addNew(n, k, item, deque); r++;
            item = next(item.xi, item.i);

            result = result ^ windowMinimum(deque, l, r);
        }

        return result;
    }
}