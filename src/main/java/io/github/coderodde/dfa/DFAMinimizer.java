package io.github.coderodde.dfa;

import java.util.Set;

/**
 * This simple interface defines the API for DFA minimization algorithms.
 */
public interface DFAMinimizer {
    
    /**
     * Minimizes the target DFA.
     * 
     * @param target the DFA to minimize.
     * 
     * @return the minimized DFA.
     */
    public DFA minimize(DFA target);
    
    /**
     * Returns the set {@code a} setminus {@code b}.
     * 
     * @param a the set to minus from.
     * @param b the set to minus.
     * @return {@code a \setminus b}.
     */
    public static Set<Integer> setminus(Set<Integer> a, Set<Integer> b) {
        a.removeAll(b);
        return a;
    }
}
