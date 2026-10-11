package dev.priyanshu.leetcode.search.binary;

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

public interface MaximumRunningTimeOfNComputers {
    long maxRunTime(int n, int[] batteries);
}

class MaximumRunningTimeOfNComputersImpl implements MaximumRunningTimeOfNComputers {

    private long tot(int[] batteries){
        long sum = 0;
        for(int battery: batteries){
            sum += battery;
        }

        return sum;
    }

    private boolean check(int n, int[] batteries, long mid){
        long tot = mid * n;

        for(int battery: batteries){
            tot -= Math.min(battery, mid);
            if(tot <= 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Alternative to {@link #check(int, int[], long)}: directly simulates the optimal
     * battery-swapping strategy (port of heap_strategy from test_strategy7.py) instead of
     * using it as a binary-search predicate. Keeps an "active" min-heap of the n batteries
     * currently powering computers (with a lazily applied offset for elapsed time) and a
     * "waiting" max-heap of the remaining batteries, repeatedly swapping the weakest active
     * battery with the strongest waiting one whenever it becomes favorable.
     */
    public long simulate(int n, int[] batteries){
        Integer[] sorted = Arrays.stream(batteries).boxed().toArray(Integer[]::new);
        Arrays.sort(sorted, Collections.reverseOrder());

        PriorityQueue<Long> activeHeap = new PriorityQueue<>();
        for(int i = 0; i < n && i < sorted.length; i++){
            activeHeap.offer((long) sorted[i]);
        }

        PriorityQueue<Long> waitingHeap = new PriorityQueue<>(Collections.reverseOrder());
        for(int i = n; i < sorted.length; i++){
            waitingHeap.offer((long) sorted[i]);
        }

        long activeOffset = 0;
        long total = 0;

        while(activeHeap.size() >= n){
            long a = activeHeap.peek() - activeOffset;
            if(a <= 0){
                break;
            }

            if(waitingHeap.isEmpty()){
                total += a;
                activeHeap.poll();
                continue;
            }

            long w = waitingHeap.peek();
            long delta = a > w ? a - w : 1;

            total += delta;
            activeOffset += delta;

            while(!waitingHeap.isEmpty() && !activeHeap.isEmpty()
                    && (activeHeap.peek() - activeOffset) < waitingHeap.peek()){
                long curW = waitingHeap.poll();
                long truePopped = activeHeap.poll() - activeOffset;
                activeHeap.offer(curW + activeOffset);
                waitingHeap.offer(truePopped);
            }
        }

        return total;
    }

    /**
     * Alternative to {@link #check(int, int[], long)} / binary search: a direct O(m log m)
     * greedy that avoids searching for the answer entirely.
     *
     * <p>Sort the batteries descending. Any battery whose capacity exceeds the current
     * candidate average (total / count) can never be fully depleted — it will only ever
     * supply {@code total / count} minutes before a computer's "slot" is better served by a
     * smaller battery, so it is excluded from the pool and the number of computers being
     * accounted for ({@code count}) is reduced by one. The smallest remaining batteries, on
     * the other hand, always get fully depleted. Repeat while the largest remaining battery
     * still exceeds the shrinking average; once it no longer does, {@code total / count} is
     * the answer.
     */
    public long greedy(int n, int[] batteries) {
        int[] sorted = batteries.clone();
        Arrays.sort(sorted);

        long total = 0;
        for (int battery : sorted) {
            total += battery;
        }

        int count = n;
        int i = sorted.length - 1;

        while (i >= 0 && (long) sorted[i] * count > total) {
            total -= sorted[i];
            count--;
            i--;
        }

        return total / count;
    }

    @Override
    public long maxRunTime(int n, int[] batteries) {
        long l = 1;
        long r = tot(batteries);

        while(l < r){
            long mid = l + (r-l)/2 + 1;

            if(check(n, batteries, mid)){
                l = mid;
            }else{
                r = mid-1;
            }
        }

        return l;
    }
}
