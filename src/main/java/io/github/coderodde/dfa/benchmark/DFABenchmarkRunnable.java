package io.github.coderodde.dfa.benchmark;

import io.github.coderodde.dfa.DFA;
import io.github.coderodde.dfa.DFAMinimizer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the DFA benchmark runnable.
 */
public class DFABenchmarkRunnable implements Runnable {

    private final List<DFA> dfas = new ArrayList<>();
    private final List<DFA> resultDfas = new ArrayList<>();
    private final DFAMinimizer algorithm;
    private int measured;
    
    public DFABenchmarkRunnable(DFA dfa, 
                                DFAMinimizer algorithm,
                                int iterations) {
        
        for (int i = 0; i < iterations; ++i) {
            dfas.add(DFABenchmark.copy(dfa));
        }
        
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        if (measured >= dfas.size()) {
            throw new IllegalStateException(
                String.format("%s exhausted.\n", getClass().getSimpleName()));
        }
        
        resultDfas.add(algorithm.minimize(dfas.get(measured)));
        ++measured;
    }
    
    public List<DFA> getResultDFAs() {
        return resultDfas;
    }
}
