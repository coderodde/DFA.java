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
    
    /**
     * The set of accepting states.
     */
    private final Set<Integer> acceptingStates = new HashSet<>();
    
    /**
     * The start state.
     */
    private final int startState;
    
    /**
     * The transition function.
     */
    private final TransitionFunction transitionFunction;

    /**
     * Constructs this DFA.
     * 
     * @param transitionFunction the transition function.
     * @param startState         the start state.
     * @param acceptingStates    the accepting states.
     */
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
    
    /**
     * Returns the start state.
     * 
     * @return the start state.
     */
    public int getStartState() {
        return startState;
    }
    
    /**
     * Returns an unmodifiable view over the accepting states.
     * 
     * @return the accepting states.
     */
    public Set<Integer> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStates);
    }
    
    /**
     * Adds the accepting state to this DFA.
     * 
     * @param state the accepting state to add.
     */
    public void addAcceptingState(int state) {
        if (!transitionFunction.getAllStates().contains(state)) {
            throw new IllegalArgumentException(
                    "Trying to add an accepting states that is not present " +
                    "in this DFA: " + state);
        }
        
        acceptingStates.add(state);
    }
    
    /**
     * Processes a single state transition.
     * 
     * @param state  the source state.
     * @param symbol the symbol.
     * 
     * @return the next state or {@code null} if the transition is not found.
     */
    public Integer process(int state, char symbol) {
        return transitionFunction.process(state, symbol);
    }
    
    /**
     * Returns the transition function.
     * 
     * @return the transition function.
     */
    public TransitionFunction getTransitionFunction() {
        return transitionFunction;
    }

    /**
     * Checks whether the input string {@code text} belongs to the regular 
     * language modelled by this DFA.
     * 
     * @param text the text to check.
     * 
     * @return {@code true} if and only if the input string belongs to the 
     *         regular language imposed by this DFA.
     */
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
    
    /**
     * Computes all unreachable states in this DFA.
     * 
     * @return the set of unreachable states.
     */
    public Set<Integer> getUnreachableStates() {
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> allStates = 
                new HashSet<>(transitionFunction.getAllStates());
        
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
    
    /**
     * Adds all missing transitions to this DFA.
     */
    public void addMissingTransitions() {
        Integer sink = null;
        List<TransitionEntry> transitionEntries = new ArrayList<>();
        
        for (int state : transitionFunction.getAllStates()) {
            for (char symbol : transitionFunction.getAlphabet()) {
                if (transitionFunction.process(state, symbol) == null) {
                    if (sink == null) {
                        sink = createRandomSinkState();
                    }
                    
                    transitionEntries.add(
                        new TransitionEntry(
                                state, 
                                sink, 
                                symbol));
                }
            }
        }
        
        for (TransitionEntry e : transitionEntries) {
            transitionFunction.addStateTransition(e.state,
                                                  e.sink, 
                                                  e.symbol);
        }
        
        if (sink != null) {
            for (char symbol : transitionFunction.getAlphabet()) {
                transitionFunction.addStateTransition(sink, sink, symbol);
            }
        }
    }
    
    /**
     * Computes and returns an union DFA composed of this DFA and {@code dfa}.
     * 
     * @param dfa the second DFA.
     * 
     * @return the union of this DFA and {@code dfa}.
     */
    public DFA union(DFA dfa) {
        Set<Character> alphabet = new HashSet<>();
        
        alphabet.addAll(this.getTransitionFunction().getAlphabet());
        alphabet.addAll( dfa.getTransitionFunction().getAlphabet());
        
        DFA dfa1 = this.normalizeAlphabet(alphabet);
        DFA dfa2 =  dfa.normalizeAlphabet(alphabet);
        
        dfa1.addMissingTransitions();
        dfa2.addMissingTransitions();
        
        dfa1.pruneUnreachableStates();
        dfa2.pruneUnreachableStates();  
        
        TransitionFunction productTransitionFunction = 
                new TransitionFunction();
        
        Set<Integer> productAcceptingState = new HashSet<>();
        Map<IntegerPair, Integer> m        = new HashMap<>();
        int id = 0;
        
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
        
        for (IntegerPair ip : m.keySet()) {
            for (char symbol : alphabet) {
                int q = ip.first;
                int p = ip.second;
                
                Integer nq = dfa1.process(q, symbol);
                Integer np = dfa2.process(p, symbol);
                
                if (nq == null || np == null) {
                    continue;
                }
                
                IntegerPair nip = new IntegerPair(nq, np);
                
                productTransitionFunction.addStateTransition(m.get(ip),
                                                        m.get(nip),
                                                        symbol);
            }
        }
        
        return new DFA(
                productTransitionFunction,
                productStartState,
                productAcceptingState);
    }
    
    public DFA normalizeAlphabet(Set<Character> alphabet) {
        
        List<TransitionEntry> entries = new ArrayList<>();
        
        TransitionFunction delta = new TransitionFunction(transitionFunction);
        int sink = createRandomSinkState();
        boolean sinkUsed = false;
        
        for (int state : delta.getAllStates()) {
            for (char symbol : alphabet) {
                if (transitionFunction.process(state, symbol) == null) {
                    if (!sinkUsed) {
                        sinkUsed = true;
                    }
                    
                    entries.add(new TransitionEntry(state, sink, symbol));
                }
            }
        }
        
        for (TransitionEntry e : entries) {
            delta.addStateTransition(e.state, e.sink, e.symbol);
        }
        
        if (sinkUsed) {
            for (char symbol : alphabet) {
                delta.addStateTransition(sink, sink, symbol);
            }
        }
        
        return new DFA(delta, 
                       startState, 
                       acceptingStates);
    }
    
    public void pruneUnreachableStates() {
        Set<Integer> unreachableStates = getUnreachableStates();
        
        for (Integer s : unreachableStates) {
            transitionFunction.function.remove(s);
            acceptingStates.remove(s);
        }
    }
    
    /** 
     * Creates a random sink state that does not yet appear in this DFA.
     * 
     * @return a random sink state. 
     */
    private Integer createRandomSinkState() {
        Random random = new Random();
        Integer sink;
        
        do {
            sink = random.nextInt();
        } while (transitionFunction.getAllStates().contains(sink));
        
        return sink;
    }

    /**
     * Used for coupling state pairs.
     */
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

    private static final class TransitionEntry {

        TransitionEntry(int state, int sink, char symbol) {
            this.state  = state;
            this.sink   = sink;
            this.symbol = symbol;
        }

        final int state;
        final int sink;
        char symbol;
    }
}
