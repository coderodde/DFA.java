package io.github.coderodde.dfa;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements the actual deterministic finite automaton.
 */
public class DFA {
    
    private final TransitionFunction transitionFunction;
    private final int startState;
    private final Set<Integer> acceptingStates;

    public DFA(TransitionFunction transitionFunction,
               int startState,
               Set<Integer> acceptingStates) {
        
        this.transitionFunction = new TransitionFunction(
                Objects.requireNonNull(transitionFunction,
                                       "Transition function is null."));
        this.startState = startState;
        this.acceptingStates = new HashSet<>(
                Objects.requireNonNull(acceptingStates,
                                       "Accepting state set is null."));
    }

    public boolean matches(String text) {
        Integer currentState = startState;

        for (char c : text.toCharArray()) {
            currentState = transitionFunction.process(currentState, c);

            if (currentState == null) {
                return false;
            }
        }

        return acceptingStates.contains(currentState);
    }
    
    public Set<Integer> getUnreachableStates() {
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> allStates = transitionFunction.getAllStates();
        
        queue.add(startState);
        
        while (!queue.isEmpty()) {
            int state = queue.removeFirst();
            
            if (visited.contains(state)) {
                continue;
            }
            
            visited.add(state);
            
            Map<Character, Integer> children = 
                    transitionFunction.function.get(state);
            
            if (children == null) {
                continue;
            }
            
            for (int child : children.values()) {
                if (!visited.contains(child)) {
                    queue.addLast(child);
                }
            }
        }
        
        allStates.removeAll(visited);
        return allStates;
    }
}
