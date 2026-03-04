package io.github.coderodde.dfa.benchmark;

import io.github.coderodde.dfa.DFA;
import io.github.coderodde.dfa.HopcroftsDFAMinimizer;
import io.github.coderodde.dfa.MooresDFAMinimizer;
import io.github.coderodde.dfa.TransitionFunction;
// https://github.com/coderodde/RunStatistics.java
import io.github.coderodde.statistics.run.RunStatistics;
import io.github.coderodde.statistics.run.Runner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class implements the benchmark for DFA minimization algorithms.
 */
public class DFABenchmark {
    
    private static final int MAXIMUM_TRANSITION_COUNT = 1578;
    private static final int STATES = 1000;
    private static final int ACCEPTING_STATES = 100;
    private static final int ITERATIONS = 10;
    
    public static void main(String[] args) {
        DFA dfa = getRandomDFA();
        System.out.print("Source DFA: ");
        printDFA(dfa);
        
        DFABenchmarkRunnable runnableMoore = 
            new DFABenchmarkRunnable(
                dfa, 
                new MooresDFAMinimizer(),
                ITERATIONS);
        
        DFABenchmarkRunnable runnableHopcroft = 
            new DFABenchmarkRunnable(
                dfa, 
                new HopcroftsDFAMinimizer(),
                ITERATIONS);
        
        RunStatistics rsMoore    = Runner.measure(runnableMoore, ITERATIONS);
        RunStatistics rsHopcroft = Runner.measure(runnableHopcroft, 
                                                  ITERATIONS);
        
        System.out.println("Moore:");
        System.out.println(rsMoore);
        
        System.out.println("---");
        
        System.out.println("Hopcroft:");
        System.out.println(rsHopcroft);
        
        System.out.println("---");
        System.out.println(
            "Algorithms agree: " + 
            matches(runnableMoore.getResultDFAs(), 
                    runnableHopcroft.getResultDFAs()));
        
        System.out.print("Result DFA: ");
        printDFA(runnableMoore.getResultDFAs().get(0));
    }
    
    public static TransitionFunction getRandomTransitionFunction() {
        TransitionFunction tf = new TransitionFunction();
        Random random = new Random(13L);
        
        for (int i = 0; i < MAXIMUM_TRANSITION_COUNT; ++i) {
            int sourceState = random.nextInt(STATES);
            int targetState = random.nextInt(STATES);
            char symbol = random.nextBoolean() ? '0' : '1';
            
            tf.addStateTransition(sourceState,
                                  targetState, 
                                  symbol);
        }
        
        return tf;
    }
    
    public static DFA getRandomDFA() {
        Random random         = new Random(666L);
        TransitionFunction tf = getRandomTransitionFunction();
        List<Integer> states  = new ArrayList<>(tf.getAllStates());
        int start             = states.get(random.nextInt(states.size()));
        
        DFA dfa = new DFA(tf, start, Set.of());
        
        for (int i = 0; i < ACCEPTING_STATES; ++i) {
            int index = random.nextInt(states.size());
            int state = states.get(index);
            dfa.addAcceptingState(state);
        }
        
        return dfa;
    }
    
    public static DFA copy(DFA dfa) {
        return new DFA(dfa.getTransitionFunction(), 
                       dfa.getStartState(), 
                       dfa.getAcceptingStates());
    }
    
    public static Set<Integer> getRandomAcceptingStates(Set<Integer> stateSet) {
        Random random = new Random(185L);
        Set<Integer> acceptingSetState = new HashSet<>();
        List<Integer> states = new ArrayList<>(stateSet);
        
        while (acceptingSetState.size() < ACCEPTING_STATES) {
            int state = states.get(random.nextInt(states.size()));
            
            if (stateSet.contains(state)) {
                acceptingSetState.add(state);
            }
        }
        
        return acceptingSetState;
    }
    
    public static boolean matches(List<DFA> dfas1, List<DFA> dfas2) {
        if (dfas1.size() != dfas2.size()) {
            return false;
        }
        
        for (int i = 0; i < dfas1.size(); ++i) {
            DFA dfa1 = dfas1.get(i);
            DFA dfa2 = dfas2.get(i);
            
            if (dfa1.getTransitionFunction().getAllStates().size() != 
                dfa2.getTransitionFunction().getAllStates().size()) {
                return false;
            }
            
            if (dfa1.getTransitionFunction().numberOfTransitions() != 
                dfa2.getTransitionFunction().numberOfTransitions()) {
                return false;
            }
        }
        
        return true;
    }
    
    private static void printDFA(DFA dfa) {
        System.out.println(
            "states: "
            + dfa.getTransitionFunction().getAllStates().size() 
            + ", transitions: " 
            + dfa.getTransitionFunction().numberOfTransitions() 
            + ", alphabet = " 
            + dfa.getTransitionFunction().getAlphabet());
    }
}
