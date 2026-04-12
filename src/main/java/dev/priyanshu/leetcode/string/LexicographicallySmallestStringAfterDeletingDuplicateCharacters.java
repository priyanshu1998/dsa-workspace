package dev.priyanshu.leetcode.string;

import java.util.Map;

/*
class Solution:
    dp = {}
    def lexSmallestAfterDeletion(self, s: str) -> str:
        if s in self.dp: return self.dp[s]

        freq = collections.Counter(s)
        # print(freq)

        M = s
        for i in range(len(s)):
            if freq[s[i]] > 1:
                v = self.lexSmallestAfterDeletion(s[:i]+s[i+1:])
                M = min(M, v)

        self.dp[s] = M
        return M


 */
public interface LexicographicallySmallestStringAfterDeletingDuplicateCharacters {
  String lexSmallestAfterDeletion(String s);
}

class LexicographicallySmallestStringAfterDeletingDuplicateCharactersImpl
    implements LexicographicallySmallestStringAfterDeletingDuplicateCharacters {

  Map<String, String> dp;

  @Override
  public String lexSmallestAfterDeletion(String s) {
    var freq = StringUtils.characterCount(s);

    var M = s;

    for (int i = 0; i < s.length(); i++) {
      if (freq.get(s.charAt(i)) > 1) {
        var v = lexSmallestAfterDeletion(s.substring(0, i) + s.substring(i + 1));

        if (v.compareTo(M) < 0) {
          M = v;
        }
      }
    }

    return M;
  }
}
