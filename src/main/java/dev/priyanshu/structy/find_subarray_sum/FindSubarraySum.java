package dev.priyanshu.structy.find_subarray_sum;


public interface FindSubarraySum {
    record Window(int l, int r){};

    int[] findSubarraySum(int[] nums, long target);


    static int[] getIndexes(Window w){
        return new int[]{w.l, w.r};
    }
}


class NaiveSolution implements FindSubarraySum{

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        for (int i=0; i<nums.length; i++){
            for (int j=1; j<=nums.length; j++){
                long sum = 0;
                for(int k=i; k<j; k++){
                    sum += nums[k];
                }
                if(sum == target){
                    var w = new Window(i, j-1);
                    return FindSubarraySum.getIndexes(w);
                } else if (sum>target){
                    break;
                }
            }
        }
        return new int[]{-1, -1};
    }
}


class SumOptimized implements FindSubarraySum {

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        long[] prefixSum = new long[nums.length];
        prefixSum[0] = nums[0];

        for(int i=1; i<nums.length; i++){
            prefixSum[i] = prefixSum[i-1] + nums[i];
        }

        for(int i=0; i<nums.length; i++){
            for(int j=1; j<=nums.length; j++){
                long sum = prefixSum[j-1] - ((i!=0)?prefixSum[i-1]:0);

                if(sum == target){
                    var w = new Window(i, j-1);
                    return FindSubarraySum.getIndexes(w);
                } else if(sum>target){
                    break;
                }
            }
        }

        return new int[]{-1, -1};
    }
}

class WindowOptimized implements FindSubarraySum {

    @Override
    public int[] findSubarraySum(int[] nums, long target) {
        long sum = nums[0];

        int l = 0;
        int r = 1;

        while(r<nums.length){
            if(sum == target){
                var w = new Window(l, r-1);
                return FindSubarraySum.getIndexes(w);
            }else if(sum > target){
                sum -= nums[l];
                l += 1;
                continue;
            }

            sum += nums[r];
            r+=1;
        }

        if(sum == target) {
            var w = new Window(l, r - 1);
            return FindSubarraySum.getIndexes(w);
        }

        return new int[]{-1, -1};
    }
}