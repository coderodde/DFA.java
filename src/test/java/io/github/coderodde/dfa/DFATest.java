package io.github.coderodde.dfa;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class DFATest {
    
    @Test
    public void getUnreachableStates() {
        TransitionFunction tf = new TransitionFunction();
        tf.addStateTransition(0, 1, 'a');
        tf.addStateTransition(2, 1, 'a');
        tf.addStateTransition(3, 1, 'b');
        tf.addStateTransition(2, 12, 'c');
        tf.addStateTransition(3, 12, 'a');
        DFA dfa = new DFA(tf, 0, Set.of());
        dfa.addAcceptingState(12);
        dfa.addAcceptingState(3);
        dfa.addAcceptingState(1);
        assertEquals(Set.of(12, 3, 1), dfa.getAcceptingStates());
        Set<Integer> unreachable = dfa.getUnreachableStates();
        assertEquals(Set.of(2, 3, 12), unreachable);
        dfa.pruneUnreachableStates();
        assertEquals(Set.of(1), dfa.getAcceptingStates());
    }
    
    @Test
    public void normalizeAlphabet() {
        TransitionFunction tf = new TransitionFunction();
        tf.addStateTransition(0, 1, 'a');
        tf.addStateTransition(2, 1, 'a');
        tf.addStateTransition(3, 1, 'b');
        tf.addStateTransition(2, 12, 'c');
        tf.addStateTransition(3, 12, 'a');
        DFA dfa = new DFA(tf, 0, Set.of());
        DFA normalized = dfa.normalizeAlphabet(Set.of('a', 'b', 'c', 'd'));
        
        assertEquals(
                Set.of('a', 'b', 'c', 'd'), 
                normalized.getTransitionFunction().getAlphabet());
    }
    
    @Test
    public void union() {
        TransitionFunction tf1 = new TransitionFunction();
        TransitionFunction tf2 = new TransitionFunction();
        
        tf1.addStateTransition(0, 0, '0');
        tf1.addStateTransition(0, 1, '1');
        tf1.addStateTransition(1, 0, '0');
        tf1.addStateTransition(1, 1, '1');
        
        tf2.addStateTransition(0, 1, '1');
        tf2.addStateTransition(1, 2, '1');
        
        DFA dfa1 = new DFA(tf1, 0, Set.of(1));
        DFA dfa2 = new DFA(tf2, 0, Set.of(2));
        
        DFA union = dfa1.union(dfa2);
        
        assertFalse(union.matches(""));
        assertFalse(union.matches("0"));
        assertFalse(union.matches("00"));
        assertFalse(union.matches("000"));
        
        assertTrue(union.matches("1"));
        assertTrue(union.matches("01"));
        assertTrue(union.matches("01111"));
    }
}
