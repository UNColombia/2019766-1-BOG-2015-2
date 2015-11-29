package funico.xover;

import funico.EquationSystem;
import funico.RandomSyntaxTree;
import unalcol.random.integer.IntUniform;
import unalcol.search.population.variation.ArityTwo;
import unalcol.types.collection.vector.Vector;

/**
 * Created by jefferson on 12/11/15.
 */
public class EquationXOver extends ArityTwo<EquationSystem> {

    @Override
    public Vector<EquationSystem> apply(EquationSystem one, EquationSystem two) {

        EquationSystem childOne = new EquationSystem(one);
        EquationSystem childTwo = new EquationSystem(two);

        int a, b, equations = childOne.getNumberOfEquations();
        IntUniform random = new IntUniform(equations);
        a = b = random.generate();
        while(a == b) b = random.generate();

        RandomSyntaxTree temp = childOne.getSyntaxTree(a);
        childOne.setSyntaxTree(a, childTwo.getSyntaxTree(b));
        childTwo.setSyntaxTree(b, temp);

        Vector<EquationSystem> toReturn = new Vector<>();
        toReturn.add(childOne);
        toReturn.add(childTwo);

        return toReturn;
    }
}