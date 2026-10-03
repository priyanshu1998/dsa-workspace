package dev.priyanshu.striver;

public interface FindPeakElement {
    int findPeakElement(int[] arr);
}

class FindPeakElementImpl implements FindPeakElement {

    @Override
    public int findPeakElement(int[] arr) {
        int l = 0;
        int r = arr.length - 1;

        while(l < r){
            int mid = (l+r)/2;

            if(mid!=arr.length-1 && arr[mid]<arr[mid+1]){
                l = mid+1;
            }else{
                r = mid;
            }
        }

        return r;
    }
}
