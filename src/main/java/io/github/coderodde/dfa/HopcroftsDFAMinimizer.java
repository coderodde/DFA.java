package io.github.coderodde.dfa;

import static io.github.coderodde.dfa.DFAMinimizer.setminus;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the Hopcrofts's DFA minimization algorithm.
 */
public class HopcroftsDFAMinimizer implements DFAMinimizer {

    @Override
    public DFA minimize(DFA target) {
        target.addMissingTransitions();
        
        Set<Character> alphabet = target.getTransitionFunction().getAlphabet();
        Set<Integer> allStates = 
                new HashSet<>(target.getTransitionFunction().getAllStates());
        
        Set<Integer> F = new HashSet<>(target.getAcceptingStates());
        
        List<Set<Integer>> P = new ArrayList<>();
        Set<Integer> acc = new HashSet<>(F);
        Set<Integer> rej = new HashSet<>(allStates);
        rej.removeAll(acc);
        
        if (!acc.isEmpty()) {
            P.add(acc);
        }
        
        if (!rej.isEmpty()) {
            P.add(rej);
        }
        
        Deque<Pair> W  = new ArrayDeque<>();
        Set<Pair> setW = new HashSet<>();
        Set<Integer> S = new HashSet<>();
        
        if (acc.size() <= rej.size()) {
            S = acc;
        } else {
            S = rej;
        }
        
        for (char symbol : alphabet) {
            Pair p = new Pair(S, symbol);
            W.addLast(p);
            setW.add(p);
        }
        
        while (!W.isEmpty()) {
            Pair splitter = W.removeFirst();
            setW.remove(splitter);
            
            Set<Integer> pre = new HashSet<>();
            
            for (int q : allStates) {
                Integer next = target.process(q, splitter.symbol);
                
                if (next != null && splitter.states.contains(next)) {
                    pre.add(q);
                }
            }
            
            List<Set<Integer>> Pnew = new ArrayList<>();
            
            for (Set<Integer> Y : P) {
                Set<Integer> Y1 = new HashSet<>(Y);
                Y1.retainAll(pre);
                
                Set<Integer> Y2 = new HashSet<>(Y);
                Y2.removeAll(pre);
                
                if (Y1.isEmpty() || Y2.isEmpty()) {
                    Pnew.add(Y);
                    continue;
                }
                
                Pnew.add(Y1);
                Pnew.add(Y2);
                
                for (char symbol : alphabet) {
                    Pair old = new Pair(Y, symbol);
                    
                    if (setW.contains(old)) {
                        setW.remove(old);
                        W.remove(old);
                        
                        Pair p1 = new Pair(Y1, symbol);
                        Pair p2 = new Pair(Y2, symbol);
                        
                        W.addLast(p1);
                        W.addLast(p2);
                        
                        setW.add(p1);
                        setW.add(p2);
                    } else {
                        Set<Integer> smaller;
                        
                        if (Y1.size() <= Y2.size()) {
                            smaller = Y1;
                        } else {
                            smaller = Y2;
                        }
                        
                        Pair np = new Pair(smaller, symbol);
                        
                        if (!setW.contains(np)) {
                            W.addLast(np);
                            setW.add(np);
                        }
                    }
                }
            }
            
            P = Pnew;
        }
        
        Map<Set<Integer>, Integer> blockToRepresentativeMap = new HashMap<>();
        Map<Integer, Integer> stateRepresentativeMap = new HashMap<>();
        
        for (Set<Integer> block : P) {
            int rep = block.iterator().next();
            blockToRepresentativeMap.put(block, rep);
            
            for (int q : block) {
                stateRepresentativeMap.put(q, rep);
            }
        }
        
        int q0 = target.getStartState();
        int q0min = stateRepresentativeMap.get(q0);
        
        Set<Integer> fmin = new HashSet<>();
        
        for (Set<Integer> block : P) {
            boolean acceptingBlock = false;
            
            for (int q : block) {
                if (F.contains(q)) {
                    acceptingBlock = true;
                    break;
                }
            }
            
            if (acceptingBlock) {
                fmin.add(blockToRepresentativeMap.get(block));
            }
        }
        
        TransitionFunction tfmin = new TransitionFunction();
        
        for (Set<Integer> block : P) {
            int rep = blockToRepresentativeMap.get(block);
            
            for (char symbol : alphabet) {
                int repNext = target.process(rep, symbol);
                int nextRep = stateRepresentativeMap.get(repNext);
                
                tfmin.addStateTransition(rep, 
                                         nextRep,
                                         symbol);
            }
        }
        
        return new DFA(tfmin, q0min, fmin);    
    }
    
    private static final class Pair {
        final Set<Integer> states;
        final char symbol;
        
        Pair(Set<Integer> states, char symbol) {
            this.states = states;
            this.symbol = symbol;
        }
    }
}
