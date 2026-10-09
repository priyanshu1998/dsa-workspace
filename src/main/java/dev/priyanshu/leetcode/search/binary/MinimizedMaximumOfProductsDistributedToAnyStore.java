package dev.priyanshu.leetcode.search.binary;

public interface MinimizedMaximumOfProductsDistributedToAnyStore {
    int minimizedMaximum(int n, int[] quantities);
}

class MinimizedMaximumOfProductsDistributedToAnyStoreImpl implements MinimizedMaximumOfProductsDistributedToAnyStore{

    private int max(int []quantities){
        var M = quantities[0];
        for(var quantity: quantities){
            M = Math.max(M, quantity);
        }

        return M;
    }

    private int ciel(int p, int q){
        return (p + q - 1) / q;
    }

    private boolean check(int n, int[] quantities, int x){
        int count = 0;

        for(int quantity: quantities){
            count += ciel(quantity, x);    // r < x | x, x, ..., x, r
        }

//        System.out.printf("x = %d, count=%d \n", x, count);
        return count <= n;
    }

    @Override
    public int minimizedMaximum(int n, int[] quantities) {
        int l = 1;
        int r = max(quantities);

        while (l<r){
            int mid = l + (r-l)/2;
            if(check(n, quantities, mid)){
                r = mid;
            }else{
                l = mid+1;
            }
        }

        return r;
    }
}
