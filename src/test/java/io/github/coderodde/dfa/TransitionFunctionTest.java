package io.github.coderodde.dfa;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransitionFunctionTest {

    @Test
    public void actualAlphabet() {
        TransitionFunction tf = new TransitionFunction();
        tf.addStateTransition(1, 2, 'a');
        tf.addStateTransition(1, 3, 'b');
        tf.addStateTransition(1, 4, 'c');
        tf.addStateTransition(1, 5, 'b');
        
        assertEquals(Set.of('a', 'b', 'c'), tf.getAlphabet());
    }
}
