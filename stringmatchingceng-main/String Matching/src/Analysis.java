import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    public Naive() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}

class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    public KMP() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Compute LPS (Longest Proper Prefix which is also Suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered.");
    }

    public RabinKarp() {
    }

    private static final int PRIME = 101; // A prime number for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Number of characters in the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Calculate hash value for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textHash) {
                // Check characters one by one
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }

            // Calculate hash value for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Convert negative hash to positive
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * TODO: Implement Boyer-Moore algorithm
 * This is a homework assignment for students
 */

class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }


        Map<Character, Integer> badChar = new HashMap<>();
        badCharHeuristic(pattern, m, badChar);

        int[] goodSuffix = new int[m + 1];
        preprocessGoodSuffix(pattern, m, goodSuffix);

        int s = 0;
        while (s <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                indices.add(s);
                s += goodSuffix[0];
            } else {

                char textChar = text.charAt(s + j);
                int badCharIndex = badChar.getOrDefault(textChar, -1);

                int badCharShift = Math.max(1, j - badCharIndex);
                int goodSuffixShift = goodSuffix[j + 1];

                s += Math.max(badCharShift, goodSuffixShift);
            }
        }

        return indicesToString(indices);
    }


    private void badCharHeuristic(String pattern, int size, Map<Character, Integer> badChar) {
        for (int i = 0; i < size; i++) {
            badChar.put(pattern.charAt(i), i);
        }
    }


    private void preprocessGoodSuffix(String pattern, int m, int[] goodSuffix) {
        int[] suffixes = new int[m];
        computeSuffixes(pattern, m, suffixes);

        for (int i = 0; i <= m; i++) {
            goodSuffix[i] = m;
        }

        int j = 0;
        for (int i = m - 1; i >= -1; i--) {
            if (i == -1 || suffixes[i] == i + 1) {
                for (; j < m - 1 - i; j++) {
                    if (goodSuffix[j] == m) {
                        goodSuffix[j] = m - 1 - i;
                    }
                }
            }
        }

        for (int i = 0; i <= m - 2; i++) {
            goodSuffix[m - suffixes[i]] = m - 1 - i;
        }
    }


    private void computeSuffixes(String pattern, int m, int[] suffixes) {
        suffixes[m - 1] = m;
        int g = m - 1;
        int f = 0;

        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suffixes[i + m - 1 - f] < i - g) {
                suffixes[i] = suffixes[i + m - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f)) {
                    g--;
                }
                suffixes[i] = f - g;
            }
        }
    }
}
/**
 * * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
 */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // STRATEGY 1: Internal "Tiny" check
        // If pattern is too small, complex heuristics perform worse than brute force.
        if (m <= 2) {
            // Simple Naive Loop for tiny patterns
            for (int i = 0; i <= n - m; i++) {
                int j = 0;
                while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                    j++;
                }
                if (j == m) indices.add(i);
            }
            return indicesToString(indices);
        }

        // STRATEGY 2: Sunday's Preprocessing
        // We create a "Shift Table" for the character *after* the window.
        // shift[c] = m + 1 (default)
        // shift[c] = m - last_index_of_c (if c is in pattern)
        int[] shiftTable = new int[256];
        for (int i = 0; i < 256; i++) {
            shiftTable[i] = m + 1;
        }
        for (int i = 0; i < m; i++) {
            shiftTable[pattern.charAt(i)] = m - i;
        }

        // STRATEGY 3: Raita's Comparison Order
        // Prepare specific characters for the "Tunnel" check
        char firstCh = pattern.charAt(0);
        char middleCh = pattern.charAt(m / 2);
        char lastCh = pattern.charAt(m - 1);

        int s = 0; // Current shift

        // Loop while the pattern window fits in the text
        while (s <= n - m) {
            char textLastChar = text.charAt(s + m - 1);

            // 1. Check Last Character (Highest probability of mismatch)
            if (textLastChar == lastCh) {
                // 2. Check First Character
                if (text.charAt(s) == firstCh) {
                    // 3. Check Middle Character
                    if (text.charAt(s + m / 2) == middleCh) {
                        // 4. Check the rest (Standard scan)
                        // We already checked 0, m/2, and m-1. Scan the gaps.
                        int j = 1;
                        while (j < m - 1 && text.charAt(s + j) == pattern.charAt(j)) {
                            j++;
                        }

                        // Did we finish the loop?
                        if (j >= m - 1) {
                            indices.add(s);
                        }
                    }
                }
            }

            // STRATEGY 4: Sunday's Jump Heuristic
            // We look at the character immediately *after* the current window.
            // If s + m >= n, we are at the end and can't jump anymore.
            if (s + m < n) {
                // The character at text[s+m] decides the jump!
                s += shiftTable[text.charAt(s + m)];
            } else {
                s += 1; // Standard step at the very end
            }
        }

        return indicesToString(indices);
    }
}

