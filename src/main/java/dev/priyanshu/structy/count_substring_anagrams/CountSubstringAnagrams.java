package dev.priyanshu.structy.count_substring_anagrams;

public interface CountSubstringAnagrams {
  int countSubstringAnagrams(String s, String anagram);
}

class OptimizedSolution implements CountSubstringAnagrams {

  @Override
  public int countSubstringAnagrams(String s, String anagram) {
    int k = anagram.length();
    if (k > s.length()) return 0;

    int[] anagramFreq = new int[26];
    int[] windowFreq = new int[26];

    for (int i = 0; i < k; i++) {
      anagramFreq[anagram.charAt(i) - 'a']++;
      windowFreq[s.charAt(i) - 'a']++;
    }

    int mismatches = 0;
    for (int i = 0; i < 26; i++) {
      if (anagramFreq[i] != windowFreq[i]) mismatches++;
    }

    int count = 0;
    if (mismatches == 0) count++;

    for (int r = k; r < s.length(); r++) {
      int l = r - k;
      int addIdx = s.charAt(r) - 'a';
      int popIdx = s.charAt(l) - 'a';

      if (windowFreq[addIdx] == anagramFreq[addIdx]) mismatches++;
      else if (windowFreq[addIdx] == anagramFreq[addIdx] - 1) mismatches--;
      windowFreq[addIdx]++;

      if (windowFreq[popIdx] == anagramFreq[popIdx]) mismatches++;
      else if (windowFreq[popIdx] == anagramFreq[popIdx] + 1) mismatches--;
      windowFreq[popIdx]--;

      if (mismatches == 0) {
        count++;
      }
    }

    return count;
  }
}
