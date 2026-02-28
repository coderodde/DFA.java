package io.github.coderodde.dfa;

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
        DFA dfa = new DFA(tf, 0, Set.of());
        Set<Integer> unreachable = dfa.getUnreachableStates();
        assertEquals(Set.of(2, 3), unreachable);
    }
}
