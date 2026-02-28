package io.github.coderodde.dfa;

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
    
    public TransitionFunction() {
        
    }
    
    /**
     * Copy constructor.
     * 
     * @param tf the transition function to copy.
     */
    public TransitionFunction(TransitionFunction tf) {
        for (Integer i : tf.function.keySet()) {
            this.function.put(i, new HashMap<>(tf.function.get(i)));
        }
    }

    public void setTransition(int startState, 
                              int goalState,
                              char character) {
        function.putIfAbsent(startState, new HashMap<>());
        function.get(startState).put(character, goalState);
    }

    public Integer process(int startState, char character) {
        Map<Character, Integer> m = function.get(startState);
        return m == null ? null : m.get(character);
    }
    
    public Set<Character> getActualAlphabet() {
        Set<Character> alphabet = new HashSet<>();
        
        for (Map<Character, Integer> m : function.values()) {
            alphabet.addAll(m.keySet());
        }
        
        return alphabet;
    }
    
    public Set<Integer> getAllStates() {
        Set<Integer> states = new HashSet<>();
        
        for (Integer i : function.keySet()) {
            states.add(i);
            
            for (Integer j : function.get(i).values()) {
                states.add(j);
            }
        }
        
        return states;
    }
}
