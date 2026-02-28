package io.github.coderodde.dfa;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements a transition function for DFAs.
 */
public class TransitionFunction {

    final Map<Integer, Map<Character, Integer>> function = 
          new HashMap<>();
    
    private final Set<Character> alphabet = new HashSet<>();
    private final Set<Integer>   states   = new HashSet<>();
    
    public TransitionFunction() {
        
    }
    
    /**
     * Copy constructor.
     * 
     * @param tf the transition function to copy.
     */
    public TransitionFunction(TransitionFunction tf) {
        for (Map.Entry<Integer, Map<Character, Integer>> e1 
                : tf.function.entrySet()) {
            
            int state = e1.getKey();
            
            if (!this.function.containsKey(state)) {
                this.function.put(state, new HashMap<>());
            }
            
            for (Map.Entry<Character, Integer> e2 : e1.getValue().entrySet()) {
                this.function.get(state).put(e2.getKey(), e2.getValue());
            }
            
            this.states.addAll(tf.function.get(state).values());
        }
        
        this.states.addAll(tf.function.keySet());
        this.alphabet.addAll(tf.getAlphabet());
    }

    public void setTransition(int startState, 
                              int goalState,
                              char character) {
        alphabet.add(character);
        states.add(startState);
        states.add(goalState);
        function.putIfAbsent(startState, new HashMap<>());
        function.get(startState).put(character, goalState);
    }

    public Integer process(int startState, char character) {
        Map<Character, Integer> m = function.get(startState);
        return m == null ? null : m.get(character);
    }
    
    public Set<Character> getAlphabet() {
        return Collections.unmodifiableSet(alphabet);
    }
    
    public Set<Integer> getAllStates() {
        return states;
    }
}
