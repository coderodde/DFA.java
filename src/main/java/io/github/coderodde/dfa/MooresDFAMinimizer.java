package io.github.coderodde.dfa;

import static io.github.coderodde.dfa.DFAMinimizer.setminus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the Moore's algorithm for DFA minimization.
 */
public class MooresDFAMinimizer implements DFAMinimizer {

    @Override
    public DFA minimize(DFA target) {
        target.pruneUnreachableStates();
        target.addMissingTransitions();
        
        List<Set<Integer>> P = new ArrayList<>();
        
        Set<Integer> bacc = target.getAcceptingStates();
        Set<Integer> brej = 
            setminus(
                new HashSet<>(target.getTransitionFunction().getAllStates()),
                new HashSet<>(target.getAcceptingStates()));
        
        if (!bacc.isEmpty()) {
            P.add(bacc);
        }
        
        if (!brej.isEmpty()) {
            P.add(brej);
        }
        
        Map<Integer, Integer> blockIdMap = buildBlockIdMap(P);
        
        boolean changed = true;
        
        while (changed) {
            changed = false;
            
            List<Set<Integer>> Pnew = new ArrayList<>();
            
            for (Set<Integer> block : P) {
                Map<List<Integer>, Set<Integer>> groups = new HashMap<>();
                
                for (Integer q : block) {
                    List<Integer> signature = new ArrayList<>();
                    
                    for (char symbol 
                            : target.getTransitionFunction().getAlphabet()) {
                        
                        int nextState = target.process(q, symbol);
                        int blockId   = blockIdMap.get(nextState);
                        signature.add(blockId);
                    }
                    
                    groups.computeIfAbsent(
                            signature, _ -> new HashSet<>()).add(q);
                }
                
                if (groups.size() == 1) {
                    Pnew.add(block);
                } else {
                    changed = true;
                    Pnew.addAll(groups.values());
                }
            }
            
            P = Pnew;
            blockIdMap = buildBlockIdMap(P);
        }
        
        Map<Set<Integer>, Integer> blockReprepsentativeMap = new HashMap<>();
        Map<Integer, Integer> representationOfStateMap     = new HashMap<>();
        
        for (Set<Integer> block : P) {
            int representative = block.iterator().next();
            blockReprepsentativeMap.put(block, representative);
            
            for (int q : block) {
                representationOfStateMap.put(q, representative);
            }
        }
        
        Set<Integer> qmin = new HashSet<>(blockReprepsentativeMap.values());
        int q0 = target.getStartState();
        Integer q0min = representationOfStateMap.get(q0);
        
        Set<Integer> fmin = new HashSet<>();
        Set<Integer> F = target.getAcceptingStates();
        
        for (Set<Integer> block : P) {
            boolean isAcceptingBlock = false;
            
            for (int q : block) {
                if (F.contains(q)) {
                    isAcceptingBlock = true;
                    break;
                }
            }
            
            if (isAcceptingBlock) {
                fmin.add(blockReprepsentativeMap.get(block));
            }
        }
        
        TransitionFunction tfmin = new TransitionFunction();
        Set<Character> alphabet = target.getTransitionFunction().getAlphabet();
        
        for (Set<Integer> block : P) {
            int repr = blockReprepsentativeMap.get(block);
            
            for (char symbol : alphabet) {
                int reprNext = target.getTransitionFunction()
                                     .process(repr, symbol);
                
                int nextRepr = representationOfStateMap.get(reprNext);
                tfmin.addStateTransition(repr, nextRepr, symbol);
            }
        }
        
        return new DFA(tfmin, q0min, fmin);
    }
    
    private static Map<Integer, Integer> buildBlockIdMap(List<Set<Integer>> P) {
        Map<Integer, Integer> blockId = new HashMap<>();
        
        int id = 0;
        
        for (Set<Integer> block : P) {
            for (Integer q : block) {
                blockId.put(q, id);
            }
            
            ++id;
        }
        
        return blockId;
    }
}
