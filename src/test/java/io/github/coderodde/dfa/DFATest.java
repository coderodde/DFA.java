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
}
