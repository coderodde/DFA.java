package io.github.coderodde.dfa;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class HopcroftsDFAMinimizerTest {

    @Test
    public void minimize() {
        TransitionFunction tf = new TransitionFunction();
        
        tf.addStateTransition(0, 1, '0');
        tf.addStateTransition(0, 1, '1');
        tf.addStateTransition(1, 0, '0');
        tf.addStateTransition(1, 0, '1');
        
        tf.addStateTransition(2, 3, '0');
        tf.addStateTransition(2, 3, '1');
        tf.addStateTransition(3, 2, '0');
        tf.addStateTransition(3, 2, '1');
        
        DFA dfa = new DFA(tf, 0, Set.of(0, 2));
        
        assertEquals(4, dfa.getTransitionFunction().getAllStates().size());
        
        DFA minDfa = new HopcroftsDFAMinimizer().minimize(dfa);
        
        assertEquals(2, minDfa.getTransitionFunction().getAllStates().size());
        
        assertFalse(dfa.matches("0"));
        assertFalse(minDfa.matches("0"));
        
        assertTrue(dfa.matches("00"));
        assertTrue(minDfa.matches("00"));
        
        assertTrue(dfa.matches("10"));
        assertTrue(minDfa.matches("10"));
        
        assertTrue(dfa.matches("0101"));
        assertTrue(minDfa.matches("0101"));
    
        assertFalse(dfa.matches("110"));
        assertFalse(minDfa.matches("110"));
    }
}
