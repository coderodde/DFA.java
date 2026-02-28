package io.github.coderodde.dfa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * This class implements the actual deterministic finite automaton.
 */
public class DFA {
    
    private final Set<Integer> acceptingStates = new HashSet<>();
    private final int startState;
    private final TransitionFunction transitionFunction;

    public DFA(TransitionFunction transitionFunction,
               int startState,
               Set<Integer> acceptingStates) {
        
        this.transitionFunction = new TransitionFunction(
                Objects.requireNonNull(transitionFunction,
                                       "Transition function is null."));
        this.startState = startState;
        
        for (Integer state : acceptingStates) {
            addAcceptingState(state);
        }
    }
    
    public int getStartState() {
        return startState;
    }
    
    public Set<Integer> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStates);
    }
    
    public void addAcceptingState(int state) {
        if (!transitionFunction.getAllStates().contains(state)) {
            throw new IllegalArgumentException(
                    "Trying to add an accepting states that is not present " +
                    "in this DFA: " + state);
        }
        
        acceptingStates.add(state);
    }
    
    public TransitionFunction getTransitionFunction() {
        return transitionFunction;
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
    
    public void addMissingTransiitions() {
        Integer sink = null;
        
        for (int state : transitionFunction.getAllStates()) {
            for (char symbol : transitionFunction.getAlphabet()) {
                if (transitionFunction.process(state, symbol) == null) {
                    if (sink == null) {
                        sink = createRandomSinkState();
                    }
                    
                    transitionFunction.setTransition(state,
                                                     sink, 
                                                     symbol);
                }
            }
        }
    }
    
    public DFA union(DFA dfa) {
        Set<Character> alphabet = new HashSet<>();
        alphabet.addAll(this.getTransitionFunction().getAlphabet());
        alphabet.addAll( dfa.getTransitionFunction().getAlphabet());
        
        DFA dfa1 = this.normalizeAlphabet(alphabet);
        DFA dfa2 = dfa. normalizeAlphabet(alphabet);
        
        TransitionFunction productTransitionFunction = 
                new TransitionFunction();
        
        Set<Integer> productAcceptingState = new HashSet<>();
        Map<IntegerPair, Integer> m        = new HashMap<>();
        int id = 0;
        
        for (int y : dfa1.getTransitionFunction().getAllStates()) {
            for (int x : dfa2.getTransitionFunction().getAllStates()) {
                m.put(new IntegerPair(x, y), id++);
            }
        }
        
        id = 0;
        
        for (int p : dfa1.getTransitionFunction().getAllStates()) {
            for (int q : dfa2.getTransitionFunction().getAllStates()) {
                m.put(new IntegerPair(p, q), id++);
            }
        }
        
        IntegerPair q0 = new IntegerPair(dfa1.getStartState(),
                                         dfa2.getStartState());
        
        int productStartState = m.get(q0);
        
        for (IntegerPair ip : m.keySet()) {
            int p = ip.first;
            int q = ip.second;
            
            if (dfa1.getAcceptingStates().contains(p) || 
                dfa2.getAcceptingStates().contains(q)) {
                productAcceptingState.add(m.get(ip));
            }
        }
        
        return new DFA(
                productTransitionFunction,
                productStartState,
                productAcceptingState);
    }
    
    public DFA normalizeAlphabet(Set<Character> alphabet) {
        
        class Entry {
            
            Entry(int state, int sink, char symbol) {
                this.state  = state;
                this.sink   = sink;
                this.symbol = symbol;
            }
            
            final int state;
            final int sink;
            char symbol;
        }
        
        List<Entry> entries = new ArrayList<>();
        
        TransitionFunction delta = new TransitionFunction(transitionFunction);
        int sink = createRandomSinkState();
        boolean sinkUsed = false;
        
        for (int state : delta.getAllStates()) {
            for (char symbol : alphabet) {
                if (transitionFunction.process(state, symbol) == null) {
                    if (!sinkUsed) {
                        sinkUsed = true;
                    }
                    
                    entries.add(new Entry(state, sink, symbol));
                }
            }
        }
        
        for (Entry e : entries) {
            delta.setTransition(e.state, e.sink, e.symbol);
        }
        
        if (sinkUsed) {
            for (char symbol : alphabet) {
                delta.setTransition(sink, sink, symbol);
            }
        }
        
        return new DFA(delta, 
                       startState, 
                       acceptingStates);
    }
    
    private Integer createRandomSinkState() {
        Random random = new Random();
        Integer sink;
        
        do {
            sink = random.nextInt();
        } while (transitionFunction.getAllStates().contains(sink));
        
        return sink;
    }

    private static final class IntegerPair {

        public final int first;
        public final int second;
        
        public IntegerPair(int first, int second) {
            this.first = first;
            this.second = second;
        }
        
        @Override
        public int hashCode() {
            return first ^ ~second;
        }
        
        @Override
        public boolean equals(Object object) {
            if (object instanceof IntegerPair other) {
                return first == other.first && second == other.second;
            }
            
            return false;
        }
        
        @Override
        public String toString() {
            return String.format("[%d, %d]", first, second);
        }
    }
}
