package io.github.coderodde.dfa;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransitionFunctionTest {

    @Test
    public void actualAlphabet() {
        TransitionFunction tf = new TransitionFunction();
        tf.setTransition(1, 2, 'a');
        tf.setTransition(1, 3, 'b');
        tf.setTransition(1, 4, 'c');
        tf.setTransition(1, 5, 'b');
        
        assertEquals(Set.of('a', 'b', 'c'), tf.getAlphabet());
    }
}
