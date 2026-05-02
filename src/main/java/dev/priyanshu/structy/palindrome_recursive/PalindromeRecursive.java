package dev.priyanshu.structy.palindrome_recursive;

public interface PalindromeRecursive {
    boolean isPalindrome(String s);
}

class PalindromeRecursiveImpl implements PalindromeRecursive {

    @Override
    public boolean isPalindrome(String s) {
        if (s.isEmpty() || s.length() == 1){
            return true;
        }

        int k = s.length()-1;
        return s.charAt(0) == s.charAt(k) && isPalindrome(s.substring(1, k));
    }
}

class PalindromeTailRecursiveImpl implements PalindromeRecursive {

    private boolean isPalindrome(String s, int l, int r, boolean ret){
        if(!ret){
            return false;
        }

        if (r-l == 0 || r-l==1){
            return true;
        }

        ret = s.charAt(l) == s.charAt(r);
        return isPalindrome(s, l+1, r-1, ret);
    }

    @Override
    public boolean isPalindrome(String s) {
        if(s.isEmpty()) return true;
        return isPalindrome(s, 0, s.length()-1, true);
    }
}
