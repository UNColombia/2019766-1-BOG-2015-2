package funico;

public class Node {

    public static int TERMINAL = 0;

    public Node(String name, int arity) {
        this.name = name;
        this.arity = arity;
        this.children = new Node[arity];
    }

    /**
     * For clone purposes
     */
    public Node(Node toClone) {
        this.name = toClone.name;
        this.arity = toClone.arity;
        this.children = new Node[this.arity];

        for(int i = 0; i < this.arity; ++i) {
            this.children[i] = new Node(toClone.children[i]);
        }
    }

    public String getPhenotype() {
       if(getArity() == Node.TERMINAL) {
           return name;
       }

        String toRet = name + "(";
        for(int i = 0; i < getArity(); ++i) {
            toRet += children[i].getPhenotype();
            if(i != children.length - 1)
                toRet += ", ";
        }

        return toRet + ")";
    }

    public String getName() {
        return name;
    }

    // only for successor mutation
    public void changeNode(String name, int arity) {
        if((this.arity == 0 || this.arity == 1) && arity == 1) {
            this.name = name;
            this.arity = arity;
            this.children = new Node[arity];
        }
    }

    public void deleteNode() {
        if(this.arity == 1) {
            this.arity = this.children[0].arity;
            this.name = this.children[0].name;
            this.children = this.children[0].children;
        }
    }

    public int getArity() {
        return arity;
    }

    public Node[] children;
    protected int arity;
    protected String name;
}
