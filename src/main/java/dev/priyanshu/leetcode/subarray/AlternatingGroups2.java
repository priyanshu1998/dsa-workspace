package dev.priyanshu.leetcode.subarray;

public interface AlternatingGroups2 {
    int numberOfAlternatingGroups(int[] colors, int k);
}

class AlternatingGroups2Impl implements AlternatingGroups2 {

    private record Parity(int ZeroAtEvenIndexes,
                          int OneAtEvenIndexes,
                          int ZeroAtOddIndexes,
                          int OneAtOddIndexes){}

    private void initializeGroups(int[] colors, int k, int[] groups){
        for(int i=0; i<colors.length; i++){
            groups[i] = colors[i];
        }

        for(int i=0; i<k-1; i++){
            groups[colors.length+i] = colors[i];
        }
    }

    private Parity initializeWindow(int[] groups, int k){
        int ZeroAtEvenIndexes = 0;
        int OneAtEvenIndexes = 0;
        int ZeroAtOddIndexes = 0;
        int OneAtOddIndexes = 0;
        for(int i=0; i<k; i++){
            if(i%2 == 0){
                if(groups[i] == 0){
                    ZeroAtEvenIndexes += 1;
                }else{
                    OneAtEvenIndexes += 1;
                }
            }else{
                if(groups[i] == 0){
                    ZeroAtOddIndexes += 1;
                }else{
                    OneAtOddIndexes += 1;
                }
            }
        }

        return new Parity(ZeroAtEvenIndexes, OneAtEvenIndexes, ZeroAtOddIndexes, OneAtOddIndexes);
    }

    private boolean check(Parity state){
        if(state.OneAtEvenIndexes == 0 && state.ZeroAtOddIndexes == 0){
            return true;
        }else if(state.ZeroAtEvenIndexes == 0 && state.OneAtOddIndexes == 0){
            return true;
        }
        return false;
    }

    private Parity updateWindow(Parity current, int l, int r, int[] groups){
        int ZeroAtEvenIndexes = current.ZeroAtEvenIndexes;
        int OneAtEvenIndexes = current.OneAtEvenIndexes;
        int ZeroAtOddIndexes = current.ZeroAtOddIndexes;
        int OneAtOddIndexes = current.OneAtOddIndexes;

        // remove l
        if(l%2==0){
            if(groups[l] == 0){
                ZeroAtEvenIndexes -= 1;
            }else{
                OneAtEvenIndexes -= 1;
            }
        }else{
            if(groups[l] == 0){
                ZeroAtOddIndexes -=1;
            }else{
                OneAtOddIndexes -= 1;
            }
        }

        // add r

        if(r%2==0){
            if(groups[r] == 0){
                ZeroAtEvenIndexes += 1;
            }else{
                OneAtEvenIndexes += 1;
            }
        }else{
            if(groups[r] == 0){
                ZeroAtOddIndexes +=1;
            }else{
                OneAtOddIndexes += 1;
            }
        }

        return new Parity(ZeroAtEvenIndexes, OneAtEvenIndexes, ZeroAtOddIndexes, OneAtOddIndexes);
    }

    @Override
    public int numberOfAlternatingGroups(int[] colors, int k) {
        var groups = new int[colors.length+k-1];
        initializeGroups(colors, k, groups);
        int count = 0;

        var state = initializeWindow(groups, k);
        if(check(state)){
            count += 1;
        }


        for(int i=k; i<groups.length; i++){
            state = updateWindow(state, i-k, i, groups);
            if(check(state)){
                count += 1;
            }
        }

        return count;
    }
}
