package funico.mutation;

import funico.EquationSystem;
import funico.Node;
import unalcol.random.integer.IntUniform;
import unalcol.search.population.variation.ArityTwo;
import unalcol.search.space.ArityOne;
import unalcol.types.collection.vector.Vector;

/**
 * Created by jefferson on 12/11/15.
 */
public class SuccessorMutation extends ArityOne<EquationSystem> {
    public static int ADD = 1;

    @Override
    public EquationSystem apply(EquationSystem x) {

        EquationSystem child = new EquationSystem(x);
        int toRemove, toAdd, syntaxTreeIndex = 0;
        IntUniform randEquation = new IntUniform(child.getNumberOfEquations());
        toRemove = toAdd = 0;
        int iterations = 0;
        while(toRemove < 1 && ++iterations != child.getNumberOfEquations() * 3) {
            syntaxTreeIndex = randEquation.generate();
            toRemove = child.countByArityTree(syntaxTreeIndex, 1);
        }
        if (iterations == child.getNumberOfEquations()) return child;

        toAdd = toRemove + child.countByArityTree(syntaxTreeIndex, 0);

        int add = new IntUniform(2).generate();

        // add one successor function to the three
        if(add == ADD) {
            boolean boolOp = true;
            Node toChange = null;
            IntUniform randIndex = new IntUniform(toAdd);

            while(boolOp) {
                int index = randIndex.generate();
                toChange = child.retrieveNodeBoundedTree(syntaxTreeIndex, 0, 1, index);
                // special case of successor of booleans
                if(!toChange.getName().equals("false") && !toChange.getName().equals("true")) boolOp = false;
            }

            Node newChild = new Node(toChange);
            toChange.changeNode("s", 1);
            toChange.children[0] = newChild;
        }
        // remove it
        else {
            int index = new IntUniform(toRemove).generate();
            Node val = child.retrieveByArityTree(syntaxTreeIndex, 1, index);
            if(val != null){
                val.deleteNode();
            }
        }

        return child;
    }
}
