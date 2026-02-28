package io.github.coderodde.dfa;

import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class DFATest {
    
    @Test
    public void getUnreachableStates() {
        TransitionFunction tf = new TransitionFunction();
        tf.setTransition(0, 1, 'a');
        tf.setTransition(2, 1, 'a');
        tf.setTransition(3, 1, 'b');
        tf.setTransition(2, 12, 'c');
        tf.setTransition(3, 12, 'a');
        DFA dfa = new DFA(tf, 0, Set.of());
        Set<Integer> unreachable = dfa.getUnreachableStates();
        assertEquals(Set.of(2, 3, 12), unreachable);
    }
    
    @Test
    public void normalizeAlphabet() {
        TransitionFunction tf = new TransitionFunction();
        tf.setTransition(0, 1, 'a');
        tf.setTransition(2, 1, 'a');
        tf.setTransition(3, 1, 'b');
        tf.setTransition(2, 12, 'c');
        tf.setTransition(3, 12, 'a');
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
        
        tf1.setTransition(0, 0, '0');
        tf1.setTransition(0, 1, '1');
        tf1.setTransition(1, 0, '0');
        tf1.setTransition(1, 1, '1');
        
        tf2.setTransition(0, 1, '1');
        tf2.setTransition(1, 2, '1');
        
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
