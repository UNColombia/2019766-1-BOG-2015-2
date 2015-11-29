package funico.mutation;

import funico.EquationSystem;
import funico.Node;
import unalcol.random.integer.IntUniform;
import unalcol.search.space.ArityOne;

public class OperatorSwapMutation extends ArityOne<EquationSystem> {
    public static int MAX_ARITY = 20;

    @Override
    public EquationSystem apply(EquationSystem x) {

        EquationSystem child = new EquationSystem(x);

        int equations = child.getNumberOfEquations();
        int syntaxTreeIndex = new IntUniform(equations).generate();

        int count = child.countArityBoundedTree(syntaxTreeIndex, 2, MAX_ARITY);
        int index = new IntUniform(count).generate();

        Node selected = child.retrieveNodeBoundedTree(syntaxTreeIndex, 2, MAX_ARITY, index);
        int a, b;
        int arity = selected.getArity();
        if(arity == 2) {
            a = 0; b = 1;
        } else {
            // generating two random indices
            IntUniform random = new IntUniform(arity);
            a = b = random.generate();
            while(b == a) b = random.generate();
        }

        Node temp = selected.children[a];
        selected.children[a] = selected.children[b];
        selected.children[b] = temp;

        return child;
    }
}
