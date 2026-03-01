package io.github.coderodde.dfa;

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
        target.addMissingTransiitions();
        target.pruneUnreachableStates();
        
        List<Set<Integer>> P = new ArrayList<>();
        
        Set<Integer> bacc = target.getAcceptingStates();
        Set<Integer> brej = 
            setminus(
                target.getTransitionFunction().getAllStates(),
                target.getAcceptingStates());
        
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
                List<Integer> signature = new ArrayList<>();
                Map<List<Integer>, Set<Integer>> groups = new HashMap<>();
                
                for (Integer q : block) {
                    signature.clear();
                    
                    for (char symbol 
                            : target.getTransitionFunction().getAlphabet()) {
                        
                        int nextState = target.process(q, symbol);
                        int blockId   = blockIdMap.get(nextState);
                        signature.add(blockId);
                       
                    }
                    
                    if (!groups.containsKey(signature)) {
                        groups.put(signature, new HashSet<>());
                    }
                    
                    groups.get(signature).add(q);
                }
                
                if (groups.size() == 1) {
                    Pnew.add(block);
                } else {
                    changed = true;
                    
                    for (List<Integer> signature : groups) {
                        
                    }
                }
            }
        }
        
        return null;    
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
    
    private static Set<Integer> setminus(Set<Integer> a, Set<Integer> b) {
        a.removeAll(b);
        return a;
    }
}
