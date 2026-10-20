package dev.priyanshu.cses.sliding_window;

import dev.priyanshu.cses.FastScanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public interface SlidingWindowDistinctValues {
    int[] windowDistincts(int n, int k, int[] a);

     static int[] invoker(InputStream inputStream) throws IOException {
        var scanner = new FastScanner(inputStream);

        int n = scanner.nextInt();
        int k = scanner.nextInt();

        int[] a = new int[n];

        for(int i=0; i<n; i++){
            a[i] = scanner.nextInt();
        }

        var solver = new SlidingWindowDistinctValuesImpl();
        return solver.windowDistincts(n, k, a);
    }

    static void main(String[] args){
         try {
             var result = invoker(System.in);
             var sb = new StringBuilder(result.length * 4);
             for (int i = 0; i < result.length; i++) {
//                 System.out.printf("%d ", result[i]);
                 sb.append(result[i]);
                 sb.append(' ');
             }
             System.out.println(sb);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }
}

class SlidingWindowDistinctValuesImpl implements SlidingWindowDistinctValues {

    private static void add(Map<Integer, Integer> freq, int ai){
        freq.put(ai, freq.getOrDefault(ai, 0)+1);
    }

    private static void remove(Map<Integer, Integer> freq, int ai){
        if(freq.get(ai) == 1){
            freq.remove(ai);
        }else{
            freq.put(ai, freq.get(ai)-1);
        }
    }

    private void initializeWindow(int k, int[] a, Map<Integer, Integer> freq){
        for(int i=0; i<k; i++){
            add(freq, a[i]);
        }
    }

    @Override
    public int[] windowDistincts(int n, int k, int[] a) {
        Map<Integer, Integer> freq = new HashMap<>(k * 2);
        var result = new ArrayList<Integer>();
        initializeWindow(k, a, freq);
        result.add(freq.size());

        for(int i=k; i<n; i++){
            add(freq, a[i]);
            remove(freq, a[i-k]);
            result.add(freq.size());
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
