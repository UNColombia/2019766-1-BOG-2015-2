/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

import fplearning.language.FplConstants;
import fplearning.language.Term;

/**
 *
 * @author Camiku
 */
public class RedexIterator implements FplConstants {

    private static int nextPos = 0;
    private final static int CURRENT_ROOT = 1000;
    private final static int POSIBLE_REDEX = 1001;
    private final static int UP = 1002;
    private final static int DOWN = 1003;
    private final static int SIBLING = 1004;
    private final static int END = 1005;
    private static int maxNumberNode;
    private static int numberNode;
    private static Term redex, childDown, parentSibling, sibling, parentUp;

    public RedexIterator() {
    }

    public static void reset(Term root) {
        RedexIterator.redex = root;
        nextPos = CURRENT_ROOT;
        maxNumberNode = Evaluator.getMaxNumberNode();
        numberNode = 0;
    }

    public static Term next() {
        try {
            do {
                numberNode++;
                if (maxNumberNode != Evaluator.WITHOUT_LIMITID_REDEX && numberNode > maxNumberNode) {
                    //System.err.println(">>> max node: redex = " + numberNode + "\t" + maxNumberNode);
                    return null;
                }
                switch (nextPos) {
                    case CURRENT_ROOT:
                        redex = searchNextRoot(redex);
                        break;
                    case POSIBLE_REDEX:
                        nextPos = SIBLING;
                        break;
                    case UP:
                        redex = searchNextUp(redex);
                        break;
                    case DOWN:
                        redex = searchNextDown(redex);
                        break;
                    case SIBLING:
                        redex = searchNextSibling(redex);
                        break;
                    default:
                        throw new Exception("Variable into a ground term 0");
                }
            } while (nextPos != POSIBLE_REDEX && nextPos != END);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (nextPos == END) {
            return null;
        } else {
            return redex;
        }
    }

    public static Term searchNextRoot(Term term) throws Exception {
        switch (term.getType()) {
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                nextPos = END;
                return term;
            case SUCCESSOR:
            case LIST:
            case FUNCTOR:
                nextPos = DOWN;
                return term;
            default:
                throw new Exception("Variable into a ground term 1: " + term);
        }
    }

    public static Term searchNextDown(Term parent) throws Exception {
        childDown = parent.getListChild().getFirst();
        switch (childDown.getType()) {
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                nextPos = SIBLING;
                return childDown;
            case SUCCESSOR:
            case LIST:
            case FUNCTOR:
                nextPos = DOWN;
                return childDown;
            default:
                throw new Exception("Variable into a ground term 2: " + parent);
        }
    }

    public static Term searchNextSibling(Term left) throws Exception {
        parentSibling = left.getParent();
        if (parentSibling == null) {
            nextPos = END;
            return null;
        } else if (parentSibling.getListChild().getLast() == left) {
            nextPos = UP;
            return left;
        } else {
            sibling = parentSibling.getListChild().get(parentSibling.getListChild().indexOf(left) + 1);
            switch (sibling.getType()) {
                case NULL:
                case TRUE:
                case FALSE:
                case NATURAL:
                case CONSTANT:
                case UNDEFINED:
                    nextPos = SIBLING;
                    return sibling;
                case SUCCESSOR:
                case LIST:
                case FUNCTOR:
                    nextPos = DOWN;
                    return sibling;
                default:
                    throw new Exception("Variable into a ground term 3: " + left);
            }
        }
    }

    public static Term searchNextUp(Term child) throws Exception {
        parentUp = child.getParent();
        switch (parentUp.getType()) {
            case SUCCESSOR:
            case LIST:
                nextPos = SIBLING;
                return parentUp;
            case FUNCTOR:
                nextPos = POSIBLE_REDEX;
                return parentUp;
            default:
                throw new Exception("Variable into a ground term 4: " + child);
        }

    }

    public static boolean hasNext() {
        if (nextPos == END) {
            return false;
        } else {
            return true;
        }
    }

    public static int getMaxNumberNode() {
        return maxNumberNode;
    }

    public static void setMaxNumberNode(int maxNumberNode) {
        RedexIterator.maxNumberNode = maxNumberNode;
    }

    public static int getNumberNode() {
        return numberNode;
    }

    public static void setNumberNode(int numberNode) {
        RedexIterator.numberNode = numberNode;
    }
    
}
