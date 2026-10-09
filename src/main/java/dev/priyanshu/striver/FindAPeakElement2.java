package dev.priyanshu.striver;

public interface FindAPeakElement2 {
    int[] findPeakGrid(int[][] mat);
}

class FindAPeakElement2Impl implements FindAPeakElement2 {

    private int globalMaximum(int[][] mat, int j){
        int max = mat[0][j];
        int idx = 0;

        for(int i=0; i<mat.length; i++){
            if(mat[i][j] > max) {
                idx = i;
                max = mat[i][j];
            }
        }

        return idx;
    }

    private int[] divideAndConquer(int[][] mat, int l, int r){
        if(l > r) return new int[]{};

        int mid = (l+r)/2;
        int h = globalMaximum(mat, mid);
        int k = mid;

        if((mid == 0 || mat[h][k] > mat[h][k-1]) && (mid == mat[0].length-1 || mat[h][k] > mat[h][k+1])){
            return new int[]{h, k};
        }else if (mid > 0 && mat[h][k] < mat[h][k-1]) {
            return divideAndConquer(mat, l, mid - 1);
        } else {
            return divideAndConquer(mat, mid + 1, r);
        }
    }

    @Override
    public int[] findPeakGrid(int[][] mat) {
        return divideAndConquer(mat, 0, mat[0].length-1);
    }
}