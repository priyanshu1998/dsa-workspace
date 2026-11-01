package dev.priyanshu.leetcode.subarray;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public interface CountPrimeGapBalancedSubarrays {
    int primeSubarray(int[] nums, int k);
}

class CountPrimeGapBalancedSubarraysImpl implements CountPrimeGapBalancedSubarrays {

    private void primeSieve(boolean[] isPrime){
        Arrays.fill(isPrime, true);

        int n = isPrime.length-1;
        isPrime[0] = isPrime[1] = false;

        for(int i=2; i<=n; i++){
            if(isPrime[i]){
                for(long j = (long) i *i; j<=n; j+=i){
                    isPrime[Math.toIntExact(j)] = false;
                }
            }
        }
    }

    private void add(Deque<Integer> maxDeque,
                    Deque<Integer> minDeque,
                    int[] nums, int i){

        while(!maxDeque.isEmpty() && nums[maxDeque.getLast()] < nums[i]){
            maxDeque.removeLast();
        }
        maxDeque.addLast(i);

        while(!minDeque.isEmpty() && nums[minDeque.getLast()] > nums[i]){
            minDeque.removeLast();
        }
        minDeque.addLast(i);
    }

    private void remove(Deque<Integer> maxDeque, Deque<Integer> minDeque, int l){
        while(!maxDeque.isEmpty() && maxDeque.getFirst() <= l){
            maxDeque.removeFirst();
        }
        while(!minDeque.isEmpty() && minDeque.getFirst() <= l){
            minDeque.removeFirst();
        }
    }

    private int primeDiff(Deque<Integer> maxDeque, Deque<Integer> minDeque, int[] nums){
        if(maxDeque.isEmpty() || minDeque.isEmpty()) return 0;
        return nums[maxDeque.getFirst()] - nums[minDeque.getFirst()];
    }

    @Override
    public int primeSubarray(int[] nums, int k) {
        boolean[] isPrime = new boolean[50_001];
        primeSieve(isPrime);

        Deque<Integer> maxDeque = new ArrayDeque<>();
        Deque<Integer> minDeque = new ArrayDeque<>();
        int cnt = 0;
        int l = 0;
        int lastPrime = -1, secondLastPrime = -1;

        for(int r=0; r<nums.length; r++){
            if(isPrime[nums[r]]){
                secondLastPrime = lastPrime;
                lastPrime = r;
                add(maxDeque, minDeque, nums, r);

                // shrink until the prime gap in [l, r] is within k
                while(primeDiff(maxDeque, minDeque, nums) > k){
                    remove(maxDeque, minDeque, l);
                    l++;
                }
            }

            // any start in [l, secondLastPrime] gives >= 2 primes with gap <= k
            if(secondLastPrime >= l){
                cnt += secondLastPrime - l + 1;
            }
        }
        return cnt;
    }
}

