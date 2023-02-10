public abstract class Node {
    String name;
    Node parent;
    Node[] children;
    int numOfChildren;

    Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
        numOfChildren = 0;
        // Can add more children with more skills
        int maxChildren = 5;
        children = new Node[maxChildren];
    }

    public String getName() {
        return this.name;
    }

    public Node getParent() {
        return this.parent;
    }

    public void addChild(Node child) {
        children[numOfChildren] = child;
        numOfChildren++;
    }

    public void removeChild(Node child, int index) {
        if(children.length - 1 - index >= 0)
            System.arraycopy(children, index + 1, children, index, children.length - 1 - index);
        numOfChildren--;
    }

    public Node getChild(int index) {
        return children[index];
    }

    public void initiateAction() {}
}
