package funico;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import unalcol.random.integer.IntUniform;
import unalcol.types.collection.vector.Vector;

public class RandomSyntaxTree {
    public RandomSyntaxTree(String[][] examples, String[] variables, String[] functor, int[] arityFun, String[] terminals, int levels) {
        this.arityFun = arityFun;
        this.functor = functor;
        this.terminals = terminals;
        this.levels = levels;
        this.examples = examples;
        this.variables = variables;

        generateRandomTree(this.levels);
    }

    /**
     * For clone purposes (small copy)
     */
    public RandomSyntaxTree(RandomSyntaxTree toClone) {
        this.root = new Equal((Equal) toClone.root);
    }

    public String getPhenotype() {
        return root.getPhenotype();
    }

    private class VoidNode {
        public Node prevNode;
        public int level;
        public int index;

        public VoidNode(Node prevNode, int level, int index) {
            this.prevNode = prevNode;
            this.level = level;
            this.index = index;
        }
    }

    private void generateConstraints() {
        HashMap<String, Vector<String>> map = new HashMap<>();

        int max = 0;
        boolean isNumLeft = false;
        boolean isBoolLeft = false;
        boolean isBoolright = false;
        String[] splitted;
        String right;

        for(int i = 0; i < this.examples.length; ++i) {
            splitted = this.examples[i][0].split(",");
            right = this.examples[i][1];

            int val = splitted.length;
            String last = splitted[splitted.length - 1];
            // TODO: deal with case of length 1

            last = last.replaceAll("[, )]", "");

            // for the left side
            if(last.equals("true") || last.equals("false")) isBoolLeft = true;
            if(last.matches("^-?\\d+$")) isNumLeft = true;

            // for the right side
            if(right.equals("true") || right.equals("false")) isBoolright = true;

            max =  val > max ? val : max;
        }


        // generate successor constraint
        Vector<String> vars = new Vector<>();
        vars.add("0");
        for(int i = 0; i < max; ++i) {
            vars.add(this.variables[i]);
        }
        map.put("s", vars);

        // generate equal constraint
        vars = new Vector<>();
        if(isBoolright) {
            vars.add("false");
            vars.add("true");
        } else {
            vars.add("0");
            for(int i = 0; i < max; ++i) {
                vars.add(this.variables[i]);
            }
        }
        map.put("=", vars);

        // current function
        vars = new Vector<>();
        if(isNumLeft) {
            vars.add("0");
            for(int i = 0; i < max; ++i) {
                //System.out.println(this.variables[i]);
                vars.add(this.variables[i]);
            }
        }
        if(isBoolLeft) {
            vars.add("false");
            vars.add("true");
        }
        map.put(functor[0], vars);

        //System.out.println(map.get("s").get(0).toString());

        this.map = map;
    }

    private void generateRandomTree(int levels) {
        generateConstraints();

        root = new Equal();
        root.children[0] = new Node(functor[0], arityFun[0]);
        Node currentLeft = root.children[0];

        IntUniform randomTerminals;
        IntUniform randomFunctions = new IntUniform(this.functor.length);
        IntUniform randomChoose = new IntUniform(2);

        Stack<VoidNode> toCreate = new Stack<>();
        HashSet<String> leftTerminals = new HashSet<>();

        // add basic operators that doesn't depend of other variables
        leftTerminals.add("0");
        leftTerminals.add("true");
        leftTerminals.add("false");

        for(int j = 0; j < 2; ++j) {
            if(j == 0) {
                // creating left nodes
                for(int i = 0; i < arityFun[0]; ++i) {
                    toCreate.push(new VoidNode(currentLeft, 3, i));
                }
            } else {
                // to create right tree
                toCreate.push(new VoidNode(root, 2, 1));
            }

            // create left tree
            while(!toCreate.empty()) {
                VoidNode nodeToCrt = toCreate.pop();
                Vector<String> currentTerminals = map.get(nodeToCrt.prevNode.getName());
                randomTerminals = new IntUniform(currentTerminals.size());

                Node previousNode = nodeToCrt.prevNode;

                int func = randomChoose.generate();
                if(nodeToCrt.level == levels || func == 0) {
                    int index = randomTerminals.generate();

                    // check terminal for the right tree
                    while(j == 1 && !leftTerminals.contains(currentTerminals.get(index)))
                        index = randomTerminals.generate();

                    previousNode.children[nodeToCrt.index] = new Node(currentTerminals.get(index), Node.TERMINAL);

                    // save for the right operators
                    leftTerminals.add(currentTerminals.get(index));
                } else {

                    int index = randomFunctions.generate();

                    Node currentNode = new Node(this.functor[index], this.arityFun[index]);
                    previousNode.children[nodeToCrt.index] = currentNode;

                    for(int i = 0; i < this.arityFun[index]; ++i) {
                        toCreate.push(new VoidNode(currentNode, nodeToCrt.level + 1, i));
                    }
                }
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    private Node root;
    private String[] functor;
    private String[] terminals;
    private String[] variables;
    private String[][] examples;
    private int[] arityFun;
    private int levels;
    private HashMap<String, Vector<String>> map;
}
