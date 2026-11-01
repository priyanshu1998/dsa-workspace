package dev.priyanshu.leetcode.subarray;

import java.util.HashSet;

public interface DistinctPointsReachableAfterSubstringRemoval {
    int distinctPoints(String s, int k);
}

class DistinctPointsReachableAfterSubstringRemovalImpl implements DistinctPointsReachableAfterSubstringRemoval {

    private record Delta (int x, int y) {
        public Delta update(char c){
            if( c == 'U'){
                return new Delta(x, y+1);
            }else if(c == 'D'){
                return new Delta(x, y -1);
            }else if(c == 'L'){
                return new Delta(x-1, y);
            }else{
                return new Delta(x+1, y);
            }
        }


        public Delta removeL(char c){
            if( c == 'U'){
                return new Delta(x, y-1);
            }else if(c == 'D'){
                return new Delta(x, y +1);
            }else if(c == 'L'){
                return new Delta(x+1, y);
            }else{
                return new Delta(x-1, y);
            }
        }

        public Delta addR(char c){
            return this.update(c);
        }

        private static Delta completeWalk(String s){
            var pos = new Delta(0, 0);

            for(char c:s.toCharArray()){
                pos = pos.update(c);
            }
            return pos;
        }
    }


    private Delta initialize(String s, int k){
        var delta = new Delta(0, 0);
        for(int i=0; i<k; i++){
            char c = s.charAt(i);
            delta = delta.update(c);
        }
        return delta;
    }

    @Override
    public int distinctPoints(String s, int k) {
        var windowDelta = initialize(s, k);
        var finalDelta = Delta.completeWalk(s);
        var set = new HashSet<Delta>();

        int x = finalDelta.x - windowDelta.x;
        int y = finalDelta.y - windowDelta.y;
        set.add(new Delta(x, y));

        for(int i=k; i<s.length(); i++){
            windowDelta = windowDelta.removeL(s.charAt(i-k));
            windowDelta = windowDelta.addR(s.charAt(i));

            x = finalDelta.x - windowDelta.x;
            y = finalDelta.y - windowDelta.y;

            set.add(new Delta(x, y));
        }

        return set.size();
    }
}
