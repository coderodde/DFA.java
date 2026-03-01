package io.github.coderodde.dfa;

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
}
