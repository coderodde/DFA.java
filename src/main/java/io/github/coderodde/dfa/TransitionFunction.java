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
    
    /**
     * The number of state transitions.
     */
    private int numberOfTransitions;

    /**
     * The actual data store for holding the state transitions. Is accessed by 
     * {@link DFA}, therefore package-private.
     */
    final Map<Integer, Map<Character, Integer>> function = new HashMap<>();
    
    /**
     * The current used alphabet.
     */
    private final Set<Character> alphabet = new HashSet<>();
    
    /**
     * The current used states.
     */
    private final Set<Integer> states = new HashSet<>();
    
    /**
     * Constructs an empty transition function with no state transitions.
     */
    public TransitionFunction() {
        
    }
    
    /**
     * Copy constructor.
     * 
     * @param tf the transition function to copy.
     */
    public TransitionFunction(TransitionFunction tf) {
        numberOfTransitions = tf.numberOfTransitions;
        
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

    /**
     * Adds a new state transition only if it is not already present.
     * 
     * @param startState the start state of the state transition.
     * @param goalState  the goal state of the state transition.
     * @param character  the target symbol.
     */
    public void addStateTransition(int startState, 
                                   int goalState,
                                   char character) {
        Map<Character, Integer> mapping = 
                function.computeIfAbsent(startState, _ -> new HashMap<>());
        
        Integer old = mapping.put(character, goalState);
        
        if (old == null) {
            ++numberOfTransitions;
            alphabet.add(character);
            states.add(startState);
            states.add(goalState);
        } else if (!old.equals(goalState)) {
            rebuildCaches();
        }
    }

    /**
     * Processes a state transition.
     * 
     * @param startState the source start state.
     * @param character  the symbol on which to follow the link.
     * @return the next state or {@code null} if there is no such transition.
     */
    public Integer process(int startState, char character) {
        Map<Character, Integer> m = function.get(startState);
        return m == null ? null : m.get(character);
    }
    
    /**
     * Returns the unmodifiable view of the entire alphabet so far.
     * 
     * @return the alphabet.
     */
    public Set<Character> getAlphabet() {
        return Collections.unmodifiableSet(alphabet);
    }
    
    /**
     * Returns the unmodifiable view of the entire state set so far.
     * 
     * @return the state set. 
     */
    public Set<Integer> getAllStates() {
        return Collections.unmodifiableSet(states);
    }
    
    /**
     * Returns the number of distinct state transitions.
     * 
     * @return the number of state transitions.
     */
    public int numberOfTransitions() {
        return numberOfTransitions;
    }
    
    private void rebuildCaches() {
        states.clear();
        alphabet.clear();
        numberOfTransitions = 0;
        
        for (var e : function.entrySet()) {
            int from = e.getKey();
            states.add(from);
            
            for (var t : e.getValue().entrySet()) {
                alphabet.add(t.getKey());
                states.add(t.getValue());
                ++numberOfTransitions;
            }
        }
    }
}
