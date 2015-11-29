package funico;

public class Equal extends Node{
    public Equal() {
        super("=", 2);
    }

    public Equal(Equal toClone) {
        super("=", 2);

        for(int i = 0; i < 2; ++i) {
            this.children[i] = new Node(toClone.children[i]);
        }
    }

    @Override
    public String getPhenotype() {
        return children[0].getPhenotype() + this.name + children[1].getPhenotype();
    }
}
