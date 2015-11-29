/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Parser implements FplConstants {

    private static int type_variable = -1, n, i, numberline;
    private static Token token = null;
    private static final Token tokenException = null;
    private static Term child = null;
    private static Term parent = null;
    private static final LinkedList<Byte> stackArgs = new LinkedList();
    private static LinkedList<Term> listTerm = null;
    private static LinkedList<Token> listToken = null;
    private static LinkedList<String> stackTextTerms = null;
    private static final String textTerm = null;
    private static final LinkedList<Byte> stack = new LinkedList();

    public Parser() {
    }

    public static LinkedList<Term> parsing(StringBuilder sb) throws SyntacticalException, LexicalException {
        return parsing(sb.toString());
    }

    /**
     *
     * @param source
     * @return
     * @throws SyntacticalException
     * @throws LexicalException
     */
    public static LinkedList<Term> parsing(String source) throws SyntacticalException, LexicalException {
        listTerm = new LinkedList();
        numberline = 0;
        try {
            ListTokensTerms listTokensTerms = Scanner.generateTokens(source);
            listToken = listTokensTerms.getListTokens();
            stackTextTerms = listTokensTerms.getStackTextTerms();
            //System.out.println(listToken);
            //System.out.println(stackTextTerms);
            //System.out.println(stackTextTerms.size());
            stackArgs.clear();
            Term node = new Term();
            Term root = node;
            stack.clear();
            stack.addFirst(VAR_TERM);
            do {
                node = computational_step(listToken, stack, node);
                if (node == null) {
                    throw new SyntacticalException(stackTextTerms.pop());
                }
                switch (node.getType()) {
                    case EQUAL_SYMBOL:
                        node.setType(EQUAL);
                        root = node;
                        child = new Term();
                        child.setParent(root);
                        root.addChild(child);
                        node = child;
                        break;
                    case EQUAL:
                        root = node;
                        break;
                    case SEMICOLON:
                    case CARRIAGE_RETURN:
                        if (root.getType() != FINAL_TOKEN) {
                            listTerm.add(root);
                            node = new Term();
                            root = node;
                        } else {
                            node = root;
                        }
                        break;
                    case MARK_OF_BOTTOM:
                        if (root.getType() != FINAL_TOKEN) {
                            listTerm.add(root);
                        }
                        break;
                }
            } while (!stack.isEmpty());
            //System.out.println(listTerm);
            if (listTerm.isEmpty()) {
                listTerm = null;
            }
        } catch (LexicalException le) {
            listTerm = null;
            throw le;
        } catch (SyntacticalException se) {
            listTerm = null;
            throw se;
        }
        return listTerm;
    }

    private static Term computational_step(LinkedList<Token> listToken, LinkedList<Byte> stack, Term node) {
        type_variable = stack.pop();
        token = listToken.peek();
        switch (type_variable) {
            case VAR_TERM:
                stack = term(token.getType(), stack);
                break;
            case VAR_END:
                stack = end(token.getType(), stack);
                break;
            case VAR_ENDF:
                stack = endF(token.getType(), stack);
                break;
            case VAR_SEPARATOR:
                stack = separator(token.getType(), stack);
                break;
            case VAR_ARGUMENT:
                stack = argument(token.getType(), stack);
                break;
            case VAR_PARAMETER:
                stack = parameter(token.getType(), stack);
                break;
            case VAR_HEAD:
                stack = head(token.getType(), stack);
                break;
            case VAR_TAIL:
                stack = tail(token.getType(), stack);
                break;
            case VAR_LIST:
                stack = list(token.getType(), stack);
                break;
            case TRUE:
            case FALSE:
            case VARIABLE:
            case UNDEFINED:
            case EQUAL:
            case FUNCTOR:
            case SUCCESSOR:
            case CONSTANT:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    node.setValue(token.getValue());
                    node.setType(token.getType());
                } else {
                    stack = null;
                }
                break;
            case NATURAL:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    n = Integer.parseInt(token.getValue());
                    return generateTreeNumber(n, node);
                } else {
                    stack = null;
                }
                break;
            case OPEN_PARENTHESIS:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    child = new Term();
                    child.setParent(node);
                    node.addChild(child);
                    return child;
                } else {
                    stack = null;
                }
                break;
            case CLOSE_PARENTHESIS:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    return node.getParent();
                } else {
                    stack = null;
                }
                break;
            case COMMA:
                token = listToken.pop();
                if (node.getParent() != null
                        && type_variable == token.getType()) {
                    node = node.getParent();
                    child = new Term();
                    child.setParent(node);
                    node.addChild(child);
                    if (node.getType() == LIST) {
                        child.setValue(LIST_FUNCTOR);
                        child.setType(LIST);
                        node = child;
                        child = new Term();
                        child.setParent(node);
                        node.addChild(child);
                        stackArgs.push((byte) (stackArgs.pop() + 1));
                    }
                    return child;
                } else {
                    stack = null;
                }
                break;
            case OPEN_BRACKET:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    node.setValue(LIST_FUNCTOR);
                    node.setType(LIST);
                    child = new Term(null, WITHOUT_TYPE, null);
                    child.setParent(node);
                    node.addChild(child);
                    stackArgs.push((byte) 1);
                    return child;
                } else {
                    stack = null;
                }
                break;
            case CLOSE_BRACKET:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    if (node.getType() == WITHOUT_TYPE) {
                        child = node.getParent();
                        child.setValue("[]");
                        child.setType(NULL);
                        child.getListChild().clear();
                        stackArgs.pop();
                    } else if (node.getParent().getArity() == 2) {
                        n = stackArgs.pop().intValue();
                        child = node;
                        for (i = 0; i < n; i++) {
                            child = child.getParent();
                        }
                    } else {
                        node = node.getParent();
                        child = new Term();
                        child.setParent(node);
                        node.addChild(child);
                        child.setType(NULL);
                        child.setValue("[]");
                        n = stackArgs.pop().intValue();
                        for (i = 0; i < n; i++) {
                            child = child.getParent();
                        }
                    }
                    return child;
                } else {
                    stack = null;
                }
                break;
            case LIST_SEPARATOR:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    child = new Term();
                    child.setParent(node.getParent());
                    node.getParent().addChild(child);
                    return child;
                } else {
                    stack = null;
                }
                break;
            case EQUAL_SYMBOL:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    parent = new Term("equal", EQUAL_SYMBOL, null);
                    parent.addChild(node);
                    node.setParent(parent);
                    return parent;
                } else {
                    stack = null;
                }
                break;
            case SEMICOLON:
            case CARRIAGE_RETURN:
                token = listToken.pop();
                while (!stackTextTerms.pop().equals(";"));
                if (type_variable == token.getType()) {
                    return new Term(null, SEMICOLON, null);
                } else {
                    stack = null;
                }
                break;
            case MARK_OF_BOTTOM:
                token = listToken.pop();
                if (type_variable == token.getType()) {
                    return new Term(null, MARK_OF_BOTTOM, null);
                } else {
                    stack = null;
                }
                break;
            default:
                stack = null;
                break;
        }
        if (stack == null) {
            return null;
        } else {
            return node;
        }
    }

    private static LinkedList<Byte> term(int type, LinkedList<Byte> stack) {
        switch (type) {
            case TRUE:
                stack.add(0, TRUE);
                stack.add(1, VAR_END);
                break;
            case FALSE:
                stack.add(0, FALSE);
                stack.add(1, VAR_END);
                break;
            case OPEN_BRACKET:
                stack.add(0, OPEN_BRACKET);
                stack.add(1, VAR_HEAD);
                stack.add(2, VAR_END);
                break;
            case NATURAL:
                stack.add(0, NATURAL);
                stack.add(1, VAR_END);
                break;
            case CONSTANT:
                stack.add(0, CONSTANT);
                stack.add(1, VAR_END);
                break;
            case UNDEFINED:
                stack.add(0, UNDEFINED);
                stack.add(1, VAR_END);
                break;
            case SUCCESSOR:
                stack.add(0, SUCCESSOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_PARAMETER);
                stack.add(3, CLOSE_PARENTHESIS);
                stack.add(4, VAR_END);
                break;
            case EQUAL:
                stack.add(0, EQUAL);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, FUNCTOR);
                stack.add(3, OPEN_PARENTHESIS);
                stack.add(4, VAR_ARGUMENT);
                stack.add(5, VAR_SEPARATOR);
                stack.add(6, COMMA);
                stack.add(7, VAR_ARGUMENT);
                stack.add(8, CLOSE_PARENTHESIS);
                stack.add(9, VAR_END);
                break;
            case FUNCTOR:
                stack.add(0, FUNCTOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_ARGUMENT);
                stack.add(3, VAR_SEPARATOR);
                stack.add(4, VAR_ENDF);
                break;
            case CARRIAGE_RETURN:
                stack.add(0, CARRIAGE_RETURN);
                stack.add(1, VAR_TERM);
                break;
            case SEMICOLON:
                stack.add(0, SEMICOLON);
                stack.add(1, VAR_TERM);
                break;
            case MARK_OF_BOTTOM:
                stack.add(0, MARK_OF_BOTTOM);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> end(int type, LinkedList<Byte> stack) {
        switch (type) {
            case CARRIAGE_RETURN:
                stack.add(0, CARRIAGE_RETURN);
                stack.add(1, VAR_TERM);
                break;
            case SEMICOLON:
                stack.add(0, SEMICOLON);
                stack.add(1, VAR_TERM);
                break;
            case MARK_OF_BOTTOM:
                stack.add(0, MARK_OF_BOTTOM);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> endF(int type, LinkedList<Byte> stack) {
        switch (type) {
            case EQUAL_SYMBOL:
                stack.add(0, EQUAL_SYMBOL);
                stack.add(1, VAR_ARGUMENT);
                stack.add(2, VAR_END);
                break;
            case CARRIAGE_RETURN:
                stack.add(0, CARRIAGE_RETURN);
                stack.add(1, VAR_TERM);
                break;
            case SEMICOLON:
                stack.add(0, SEMICOLON);
                stack.add(1, VAR_TERM);
                break;
            case MARK_OF_BOTTOM:
                stack.add(0, MARK_OF_BOTTOM);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> separator(int type, LinkedList<Byte> stack) {
        switch (type) {
            case COMMA:
                stack.add(0, COMMA);
                stack.add(1, VAR_ARGUMENT);
                stack.add(2, VAR_SEPARATOR);
                break;
            case CLOSE_PARENTHESIS:
                stack.add(0, CLOSE_PARENTHESIS);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> argument(int type, LinkedList<Byte> stack) {
        switch (type) {
            case TRUE:
                stack.add(0, TRUE);
                break;
            case FALSE:
                stack.add(0, FALSE);
                break;
            case OPEN_BRACKET:
                stack.add(0, OPEN_BRACKET);
                stack.add(1, VAR_HEAD);
                break;
            case VARIABLE:
                stack.add(0, VARIABLE);
                break;
            case NATURAL:
                stack.add(0, NATURAL);
                break;
            case CONSTANT:
                stack.add(0, CONSTANT);
                break;
            case UNDEFINED:
                stack.add(0, UNDEFINED);
                break;
            case SUCCESSOR:
                stack.add(0, SUCCESSOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_PARAMETER);
                stack.add(3, CLOSE_PARENTHESIS);
                break;
            case FUNCTOR:
                stack.add(0, FUNCTOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_ARGUMENT);
                stack.add(3, VAR_SEPARATOR);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> parameter(int type, LinkedList<Byte> stack) {
        switch (type) {
            case VARIABLE:
                stack.add(0, VARIABLE);
                break;
            case NATURAL:
                stack.add(0, NATURAL);
                break;
            case CONSTANT:
                stack.add(0, CONSTANT);
                break;
            case UNDEFINED:
                stack.add(0, UNDEFINED);
                break;
            case SUCCESSOR:
                stack.add(0, SUCCESSOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_PARAMETER);
                stack.add(3, CLOSE_PARENTHESIS);
                break;
            case FUNCTOR:
                stack.add(0, FUNCTOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_ARGUMENT);
                stack.add(3, VAR_SEPARATOR);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> head(int type, LinkedList<Byte> stack) {
        switch (type) {
            case CLOSE_BRACKET:
                stack.add(0, CLOSE_BRACKET);
                break;
            case TRUE:
                stack.add(0, TRUE);
                stack.add(1, VAR_TAIL);
                break;
            case FALSE:
                stack.add(0, FALSE);
                stack.add(1, VAR_TAIL);
                break;
            case OPEN_BRACKET:
                stack.add(0, OPEN_BRACKET);
                stack.add(1, VAR_HEAD);
                stack.add(2, VAR_TAIL);
                break;
            case VARIABLE:
                stack.add(0, VARIABLE);
                stack.add(1, VAR_TAIL);
                break;
            case NATURAL:
                stack.add(0, NATURAL);
                stack.add(1, VAR_TAIL);
                break;
            case CONSTANT:
                stack.add(0, CONSTANT);
                stack.add(1, VAR_TAIL);
                break;
            case UNDEFINED:
                stack.add(0, UNDEFINED);
                stack.add(1, VAR_TAIL);
                break;
            case SUCCESSOR:
                stack.add(0, SUCCESSOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_PARAMETER);
                stack.add(3, CLOSE_PARENTHESIS);
                stack.add(4, VAR_TAIL);
                break;
            case FUNCTOR:
                stack.add(0, FUNCTOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_ARGUMENT);
                stack.add(3, VAR_SEPARATOR);
                stack.add(4, VAR_TAIL);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> tail(int type, LinkedList<Byte> stack) {
        switch (type) {
            case CLOSE_BRACKET:
                stack.add(0, CLOSE_BRACKET);
                break;
            case LIST_SEPARATOR:
                stack.add(0, LIST_SEPARATOR);
                stack.add(1, VAR_LIST);
                stack.add(2, CLOSE_BRACKET);
                break;
            case COMMA:
                stack.add(0, COMMA);
                stack.add(1, VAR_ARGUMENT);
                stack.add(2, VAR_TAIL);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static LinkedList<Byte> list(int type, LinkedList<Byte> stack) {
        switch (type) {
            case VARIABLE:
                stack.add(0, VARIABLE);
                break;
            case OPEN_BRACKET:
                stack.add(0, OPEN_BRACKET);
                stack.add(1, VAR_HEAD);
                break;
            case FUNCTOR:
                stack.add(0, FUNCTOR);
                stack.add(1, OPEN_PARENTHESIS);
                stack.add(2, VAR_ARGUMENT);
                stack.add(3, VAR_SEPARATOR);
                break;
            default:
                stack = null;
        }
        return stack;
    }

    private static Term generateTreeNumber(int n, Term root) {
        if (n == 0) {
            root.setType(NATURAL);
            root.setValue('0');
        } else {
            root.setType(SUCCESSOR);
            root.setValue('s');
            Term node = new Term("0", NATURAL, null);
            for (i = 1; i < n; i++) {
                parent = new Term("s", SUCCESSOR, null);
                node.setParent(parent);
                parent.addChild(node);
                node = parent;
            }
            root.addChild(node);
            node.setParent(root);
        }
        return root;
    }
}
