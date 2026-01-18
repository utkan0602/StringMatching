/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {
    
    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     * Tips for students:
     * - Consider the length of the text and pattern
     * - Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size
     * - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);
    
    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}


/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();

        // 1. Tiny Text Rule
        if (n < 20 || m <= 3) {
            return "Naive";
        }

        // 2. Small Alphabet Rule (DNA/Binary)
        if (isSmallAlphabetOrRepetitive(pattern)) {
            return "KMP";
        }

        return "GoCrazy";
    }

    /**
     * Helper to detect small alphabets (DNA, Binary) or highly repetitive patterns.
     * Complexity: O(min(m, 20)) -> Constant time effectively.
     */
    private boolean isSmallAlphabetOrRepetitive(String pattern) {
        // Only scan the first 20 characters to keep this check instant
        int scanLength = Math.min(pattern.length(), 20);
        int uniqueChars = 0;
        boolean[] seen = new boolean[256];

        for (int i = 0; i < scanLength; i++) {
            char c = pattern.charAt(i);
            if (!seen[c]) {
                seen[c] = true;
                uniqueChars++;
            }
        }

        // If we see fewer than 5 unique characters (e.g., A, C, G, T),
        // it is likely a small alphabet context.
        return uniqueChars < 5;
    }

    @Override
    public String getStrategyDescription() {
        return "Smart Selection: Naive for tiny inputs, KMP for DNA, and 'GoCrazy' (Sunday-Raita Hybrid) for standard text.";
    }
}

/**
 * Example implementation showing how pre-analysis could work
 * This is for demonstration purposes
 */
class ExamplePreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Simple heuristic example
        if (patternLen <= 3) {
            return "Naive"; // For very short patterns, naive is often fastest
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP"; // KMP is good for patterns with repeating prefixes
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
        } else {
            return "Naive"; // Default to naive for other cases
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}

/**
 * Instructor's pre-analysis implementation (for testing purposes only)
 * Students should NOT modify this class
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // This is a placeholder for instructor testing
        // Students should focus on implementing StudentPreAnalysis
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}
