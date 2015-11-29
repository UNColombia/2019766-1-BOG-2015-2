package funico;

import java.util.LinkedList;
import java.util.Stack;


public class EquationSystem implements Cloneable {
    public EquationSystem(int numberOfEquations, String[][] examples, String[] variables, String[] functor,
                          int[] arityFun, String[] terminals, int levels) {
        this.syntaxTree = new RandomSyntaxTree[numberOfEquations];
        this.numberOfEquations = numberOfEquations;

        for(int i = 0; i < numberOfEquations; ++i) {
            this.syntaxTree[i] = new RandomSyntaxTree(examples, variables, functor, arityFun, terminals, levels);
        }
    }

    /**
     * for clone purposes
     */
    public EquationSystem(EquationSystem toClone) {
        this.numberOfEquations = toClone.numberOfEquations;
        this.syntaxTree = new RandomSyntaxTree[this.numberOfEquations];

        for(int i = 0; i < this.numberOfEquations; ++i) {
            this.syntaxTree[i] = new RandomSyntaxTree(toClone.syntaxTree[i]);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        for(int i = 0; i < this.numberOfEquations; ++i) {
            s.append(this.syntaxTree[i].getPhenotype());
            s.append(";");
        }

        return s.toString().substring(0, s.length() - 1);
    }

    public int getNumberOfEquations() {
        return numberOfEquations;
    }

    /**
    * Function that counts the nodes that have an arity greater or equal.
    * */
    public int countArityBoundedTree(int indexSyntaxTree, int arityMin, int arityMax) {
        RandomSyntaxTree selected = this.syntaxTree[indexSyntaxTree];
        int count = 0;

        Stack<Node> s = new Stack<>();
        // we don't want the root(equal) node
        s.push(selected.getRoot().children[0]);
        s.push(selected.getRoot().children[1]);

        while(!s.empty()) {
            Node current = s.pop();
            for(int i = 0; i < current.children.length; ++i) {
                s.push(current.children[i]);
            }

            int currentArity = current.getArity();
            if(arityMin <= currentArity && currentArity <= arityMax) count++;
        }

        return count;
    }

    public int countByArityTree(int indexSyntaxTree, int arity) {
        return countArityBoundedTree(indexSyntaxTree, arity, arity);
    }

    public Node retrieveNodeBoundedTree(int indexSyntaxTree, int arityMin, int arityMax, int index) {
        RandomSyntaxTree selected = this.syntaxTree[indexSyntaxTree];
        int count = -1;

        LinkedList<Node> queue = new LinkedList<>();
        // we don't want the root(equal) node
        queue.push(selected.getRoot().children[0]);
        queue.push(selected.getRoot().children[1]);

        while(queue.size() != 0) {
            Node current = queue.pop();
            for(int i = 0; i < current.children.length; ++i) {
                queue.push(current.children[i]);
            }

            int currentArity = current.getArity();
            if(arityMin <= currentArity && currentArity <= arityMax) count++;
            if(count == index) return current;
        }

        return null;
    }

    public Node retrieveByArityTree(int indexSyntaxTree, int arity, int index) {
        return retrieveNodeBoundedTree(indexSyntaxTree, arity, arity, index);
    }

    public RandomSyntaxTree getSyntaxTree(int index) {
        return this.syntaxTree[index];
    }

    public void setSyntaxTree(int index, RandomSyntaxTree put) {
        this.syntaxTree[index] = put;
    }

    private RandomSyntaxTree[] syntaxTree;
    private int numberOfEquations;
}
