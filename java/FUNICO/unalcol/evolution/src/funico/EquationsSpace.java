package funico;

import unalcol.search.space.Space;

public class EquationsSpace extends Space<EquationSystem> {
    protected EquationSystem instance;

    public EquationsSpace(int numberOfEquations, String[][] examples, String[] variables, String[] functor,
                          int[] arityFun, String[] terminals, int levels) {

        this.numberOfEquations = numberOfEquations;
        this.examples = examples;
        this.variables = variables;
        this.functor = functor;
        this.arityFun = arityFun;
        this.terminals = terminals;
        this.levels = levels;
    }
    @Override
    public boolean feasible(EquationSystem x) {
        return false;
    }

    @Override
    public double feasibility(EquationSystem x) {
        return 0.0;
    }

    @Override
    public EquationSystem repair(EquationSystem x) {
        return x;
    }

    @Override
    public EquationSystem get() {
        EquationSystem es = new EquationSystem(numberOfEquations, examples, variables, functor, arityFun, terminals, levels);
        return es;
    }

    private int numberOfEquations;
    private String[][] examples;
    private String[] variables;
    private String[] functor;
    private int[] arityFun;
    private String[] terminals;
    private int levels;
}
