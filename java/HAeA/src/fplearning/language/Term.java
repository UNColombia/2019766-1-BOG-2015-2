/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

import fplearning.interpreter.Equation;
import fplearning.interpreter.Replacement;
import java.util.*;

/**
 *
 * @author Camiku
 */
public class Term implements FplConstants {

    private String value;
    private byte type;
    private Term parent;
    private LinkedList<Term> child = null;
    private static HashMap<String, Character> hashVarChar = new HashMap();
    private static int[] letter = new int[1];
    private static int countFreshVar;
    private static Random random;
    private static int count_suc = -1;
    private static StringBuilder sb_print = new StringBuilder();
    private static Term t_clone, t_aux;
    private static HashMap hashVariant = new HashMap<String, String>();
    private static HashSet hashVars = new HashSet<String>();
    private static LinkedList listSubTermsVar = new LinkedList<Term>();

    public Term() {
        this("", FINAL_TOKEN, null);
    }

    public Term(Term term) {
        this(term.getValue().toString(), term.getType(), null);
    }

    public Term(Token token, Term parent) {
        this(token.getValue(), token.getType(), parent);
    }

    public Term(String value, byte type, Term parent) {
        this.value = value;
        this.type = type;
        this.parent = parent;
        this.child = null;
    }

    public Term(String value, byte type) {
        this(value, type, null);
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(char value) {
        this.value = Character.toString(value);
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public byte getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Get the value of parent
     *
     * @return the value of parent
     */
    public Term getParent() {
        return parent;
    }

    /**
     * Set the value of parent
     *
     * @param parent new value of parent
     */
    public void setParent(Term parent) {
        this.parent = parent;
    }

    /**
     * Get the value of child
     *
     * @return the value of child
     */
    public LinkedList<Term> getListChild() {
        return child;
    }

    /**
     * Set the value of child
     *
     * @param child new value of child
     */
    public void setListChild(LinkedList child) {
        this.child = child;
    }

    public void addChild(Term term) {
        if (this.getListChild() == null) {
            this.child = new LinkedList<Term>();
        }
        this.getListChild().add(term);
        term.setParent(this);
    }

    public Term getChild(int i) {
        if (i < getArity()) {
            return this.child.get(i);
        } else {
            return null;
        }
    }

    public Term setChild(int i, Term term) {
        if (i < getArity()) {
            this.child.set(i, term);
            return term;
        } else {
            return null;
        }
    }

    public int getArity() {
        if (this.getListChild() == null) {
            return 0;
        } else {
            return this.getListChild().size();
        }
    }

    @Override
    public Object clone() {
        return clone(this);
    }

    public static Object clone(Term term) {
        return clone(term, new Term(term));
    }

    private static Object clone(Term term, Term parent) {
        if (term.getListChild() != null) {
            for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                t_aux = it.next();
                t_clone = new Term(t_aux);
                t_clone.setParent(parent);
                parent.addChild(t_clone);
                clone(t_aux, t_clone);
            }
        }
        return parent;
    }

    public boolean equals(Term term) {
        return equals(this, term);
    }

    private boolean equals(Term t1, Term t2) {
        if (t1.getType() == t2.getType() && t1.getValue().equals(t2.getValue())) {
            switch (t1.getType()) {
                case VARIABLE:
                case NULL:
                case TRUE:
                case FALSE:
                case NATURAL:
                case CONSTANT:
                case UNDEFINED:
                    return true;
                default:
                    if (t1.getArity() == t2.getArity()) {
                        for (Iterator<Term> it1 = t1.getListChild().iterator(),
                                it2 = t2.getListChild().iterator();
                                it1.hasNext() && it2.hasNext();) {
                            if (!equals(it1.next(), it2.next())) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
            }
        } else {
            return false;
        }
    }

    public boolean isVariableInTerm(Term var) {
        return isVariableInTerm(this, var);
    }

    public static boolean isVariableInTerm(Term term, Term var) {
        switch (term.getType()) {
            case VARIABLE:
                if (var.getType() == VARIABLE && term.getValue().equals(var.getValue())) {
                    return true;
                } else {
                    return false;
                }
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return false;
            default:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    if (isVariableInTerm(it.next(), var)) {
                        return true;
                    }
                }
                return false;
        }
    }

    public Term getVariant() {
        return getVariant(this);
    }

    public static Term getVariant(Term term) {
        hashVariant.clear();
        return getVariant(hashVariant, (Term) Term.clone(term));
    }

    private static Term getVariant(HashMap<String, String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                if (!hash.containsKey(term.getValue())) {
                    hash.put(term.getValue(), getFreshVariable());
                }
                term.setValue(hash.get(term.getValue()));
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getVariant(hash, it.next());
                }
                break;
        }
        return term;
    }

    public HashMap<String, LinkedList<Term>> getHashAllVars() {
        return getHashAllVars(this);
    }

    public static HashMap<String, LinkedList<Term>> getHashAllVars(Term term) {
        return getHashAllVars(new HashMap<String, LinkedList<Term>>(), term);
    }

    private static HashMap<String, LinkedList<Term>> getHashAllVars(HashMap<String, LinkedList<Term>> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                LinkedList list;
                if (hash.containsKey(term.getValue())) {
                    list = hash.get(term.getValue());
                } else {
                    list = new LinkedList<Term>();
                }
                list.add(term);
                hash.put(term.getValue(), list);
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    hash = getHashAllVars(hash, it.next());
                }
                break;
        }
        return hash;
    }

    public HashSet<String> getHashVars() {
        return getHashVars(this);
    }

    public static HashSet<String> getHashVars(Term term) {
        hashVars = new HashSet<String>();
        return getHashVars(hashVars, term);
    }

    private static HashSet<String> getHashVars(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                hash.add(term.getValue());
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    hash = getHashVars(hash, it.next());
                }
                break;
        }
        return hash;
    }

    public boolean hasVar() {
        return hasVar(this);
    }

    public static boolean hasVar(Term term) {
        switch (term.getType()) {
            case VARIABLE:
                return true;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    if (hasVar(it.next())) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    public LinkedList<Term> getSubTermsVars() {
        return getSubTermsVars(this);
    }

    public static LinkedList<Term> getSubTermsVars(Term term) {
        listSubTermsVar.clear();
        return getSubTermsVars(term, listSubTermsVar);
    }

    private static LinkedList<Term> getSubTermsVars(Term term, LinkedList<Term> list) {
        switch (term.getType()) {
            case VARIABLE:
                list.add(term);
                break;
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getSubTermsVars(it.next(), list);
                }
                break;
        }
        return list;
    }

    public Term getChangedConstantsByVar() {
        return getChangedConstantsByVar(this);
    }

    public static Term getChangedConstantsByVar(Term term) {
        return getChangedConstantsByVar(getHashVars(term), term).renameVar();
    }

    private static Term getChangedConstantsByVar(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                break;
            case NULL:
            /*
             String newTextValue;
             if (hash.size() > 0 && new Random().nextBoolean()) {
             newTextValue = getRandomElementHashSet(hash);
             } else {
             newTextValue = getFreshVariable();
             }
             if (new Random().nextBoolean()) {
             term.setValue(newTextValue);
             term.setType(VARIABLE);
             } else {
             term.setValue(LIST_FUNCTOR);
             term.setType(LIST);
             term.addChild(new Term(newTextValue, VARIABLE));
             term.addChild(new Term("[]", NULL));
             }
             break;
             */
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                if (hash.size() > 0 && new Random().nextBoolean()) {
                    term.setValue(getRandomElementHashSet(hash));
                } else {
                    term.setValue(getFreshVariable());
                }
                term.setType(VARIABLE);
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getChangedConstantsByVar(hash, it.next());
                }
                break;
        }
        return term;
    }

    public Term getChangedConstantsByVarLhsVarA(HashSet<String> hash) {
        return getChangedConstantsByVarLhsVarA(hash, this);
    }

    public static Term getChangedConstantsByVarLhsVarA(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                term.setValue(getRandomElementHashSet(hash));
                break;
            case SUCCESSOR:
                if (new Random().nextBoolean()) {
                    term.setValue(getRandomElementHashSet(hash));
                    term.setType(VARIABLE);
                    term.setListChild(null);
                } else {
                    getChangedConstantsByVarLhsVarA(hash, term.getChild(0));
                }
                break;
            case FUNCTOR:
            case LIST:
            case EQUAL:
            case EQUAL_SYMBOL:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getChangedConstantsByVarLhsVarA(hash, it.next());
                }
                break;
            case NULL:
            case NATURAL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                if (new Random().nextBoolean()) {
                    term.setValue(getRandomElementHashSet(hash));
                    term.setType(VARIABLE);
                }
                break;
        }
        return term;
    }

    public Term getChangedConstantsByVarLhsVarB(HashSet<String> hash) {
        return getChangedConstantsByVarLhsVarB(hash, this);
    }

    public static Term getChangedConstantsByVarLhsVarB(HashSet<String> hash, Term term) {
        switch (term.getType()) {
            case VARIABLE:
                term.setValue(getRandomElementHashSet(hash));
                break;
            case SUCCESSOR:
                if (new Random().nextBoolean()) {
                    term.setValue(getRandomElementHashSet(hash));
                    term.setType(VARIABLE);
                    term.setListChild(null);
                } else {
                    getChangedConstantsByVarLhsVarB(hash, term.getChild(0));
                }
                break;
            case FUNCTOR:
            case LIST:
            case EQUAL:
            case EQUAL_SYMBOL:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getChangedConstantsByVarLhsVarB(hash, it.next());
                }
                break;
            case NULL:
                break;
            case NATURAL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                if (new Random().nextBoolean()) {
                    term.setValue(getRandomElementHashSet(hash));
                    term.setType(VARIABLE);
                }
                break;
        }
        return term;
    }

    public LinkedList<Term> getSubTerms() {
        LinkedList<Term> list = new LinkedList<Term>();
        getSubTerms(this, list);
        return list;
    }

    private static LinkedList<Term> getSubTerms(Term term, LinkedList<Term> list) {
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                list.add(term);
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                list.add(term);
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getSubTerms(it.next(), list);
                }
                break;
        }
        return list;
    }

    public static Term replaceTerm(Term goal, Term redex, Term rhs, Replacement sigma) {
        Term newTerm = sigma.apply(rhs);
        Term term = newTerm.getChild(0);
        if (goal == redex) {
            return newTerm;
        } else {
            Term parent = redex.getParent();
            parent.getListChild().set(parent.getListChild().indexOf(redex), newTerm);
            newTerm.setParent(parent);
            return goal;
        }
    }

    public static Term getCloneReplaceTerm(Term root, Term child, Term term) {
        if (root == child) {
            return (Term) term.clone();
        } else {
            Term parentTerm = term.getParent();
            Term parentChild = child.getParent();
            parentChild.getListChild().set(parentChild.getListChild().indexOf(child), term);
            term.setParent(parentChild);
            Term clone = (Term) root.clone();
            parentChild.getListChild().set(parentChild.getListChild().indexOf(term), child);
            child.setParent(parentChild);
            term.setParent(parentTerm);
            return clone;
        }
    }

    public static Term getCloneWithOutConstants(Term term) {
        switch (term.getType()) {
            case NULL:
                break;
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                term.setType(VARIABLE);
                term.setValue(getFreshVariable());
                break;
            case VARIABLE:
                break;
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
            case LIST:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    getCloneWithOutConstants(it.next());
                }
                break;
        }
        return term;
    }

    public static String getFreshVariable() {
        return "_" + Integer.toString(Term.countFreshVar++);
    }

    public Term renameVar() {
        hashVarChar.clear();
        letter[0] = 'A';
        return renameVar(this, hashVarChar, letter);
    }

    private Term renameVar(Term term, HashMap<String, Character> hash, int[] letter) {
        switch (term.getType()) {
            case VARIABLE:
                if (!hash.containsKey(term.getValue())) {
                    hash.put(term.getValue(), new Character((char) letter[0]));
                    letter[0] = letter[0] + 1;
                }
                term.setValue(hash.get(term.getValue()).toString());
                break;
            case FUNCTOR:
            case SUCCESSOR:
            case LIST:
            case EQUAL:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    renameVar(it.next(), hash, letter);
                }
                break;
            case NULL:
            case NATURAL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                break;
        }
        return term;
    }

    public String print() {
        return print(this);
    }

    public static String print(Term term) {
        sb_print.delete(0, sb_print.length());
        return print(term, sb_print).toString();
    }

    private static StringBuilder print(Term term, StringBuilder sb) {
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case NATURAL:
            case CONSTANT:
            case UNDEFINED:
                return sb.append(term.getValue());
            case SUCCESSOR:
            case EQUAL_SYMBOL:
            case EQUAL:
            case FUNCTOR:
                sb.append(term.getValue());
                sb.append('(');
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    print(it.next(), sb);
                    if (it.hasNext()) {
                        sb.append(',');
                    } else {
                        sb.append(')');
                    }
                }
                return sb;
            case LIST:
                Term head = term.getChild(0);
                Term tail = term.getChild(1);
                sb.append("[");
                print(head, sb);
                sb.append("|");
                print(tail, sb);
                sb.append("]");
                return sb;
            default:
                System.err.println("Error printing the term: " + term.getValue());
                return null;
        }
    }

    public String printSyntacticSugar() {
        return printSyntacticSugar(this);
    }

    public static String printSyntacticSugar(Term term) {
        sb_print.delete(0, sb_print.length());
        count_suc = -1;
        return printSyntacticSugar(term, sb_print).toString();
    }

    private static StringBuilder printSyntacticSugar(Term term, StringBuilder sb) {
        switch (term.getType()) {
            case VARIABLE:
            case NULL:
            case TRUE:
            case FALSE:
            case CONSTANT:
            case UNDEFINED:
                count_suc = -1;
                sb.append(term.getValue());
                return sb;
            case NATURAL:
                if (count_suc > 0) {
                    sb.delete(sb.length() - count_suc * 2, sb.length());
                    sb.append(count_suc);
                } else {
                    sb.append(term.getValue());
                }
                count_suc = 0;
                return sb;
            case SUCCESSOR:
                sb.append('s');
                sb.append('(');
                if (count_suc > 0) {
                    count_suc++;
                } else {
                    count_suc = 1;
                }
                printSyntacticSugar(term.getChild(0), sb);
                if (count_suc == -1) {
                    sb.append(')');
                }
                return sb;
            case EQUAL_SYMBOL:
            case EQUAL:
                count_suc = -1;
                Term lhs = term.getChild(0);
                Term rhs = term.getChild(1);
                printSyntacticSugar(lhs, sb);
                sb.append(' ');
                sb.append('=');
                sb.append(' ');
                printSyntacticSugar(rhs, sb);
                count_suc = -1;
                return sb;
            case FUNCTOR:
                count_suc = -1;
                sb.append(term.getValue());
                sb.append('(');
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    printSyntacticSugar(it.next(), sb);
                    if (it.hasNext()) {
                        sb.append(',');
                    } else {
                        sb.append(')');
                    }
                }
                count_suc = -1;
                return sb;
            case LIST:
                count_suc = -1;
                sb.append('[');
                Term head;
                Term tail;
                do {
                    head = term.getChild(0);
                    tail = term.getChild(1);
                    printSyntacticSugar(head, sb);
                    switch (tail.getType()) {
                        case NULL:
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        case FUNCTOR:
                        case VARIABLE:
                            sb.append('|');
                            printSyntacticSugar(tail, sb);
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        case LIST:
                            sb.append(',');
                            term = tail;
                            count_suc = -1;
                            break;
                        case NATURAL:
                        case CONSTANT:
                        case SUCCESSOR:
                        case UNDEFINED:
                        case FALSE:
                        case TRUE:
                            //System.err.println("!!! printSyntacticSugar 0: " + tail.getValue() + " " + tail.getType());
                            sb.append('|');
                            printSyntacticSugar(tail, sb);
                            sb.append(']');
                            count_suc = -1;
                            return sb;
                        default:
                            System.err.println("!!! printSyntacticSugar 1: " + tail.getValue() + " " + tail.getType());
                            count_suc = -1;
                            return null;
                    }
                } while (true);
            default:
                System.err.println("!!! printSyntacticSugar 2: " + term.getValue() + " " + term.getType());
                return null;
        }
    }

    public String printTree() {
        return printTree(this);
    }

    public static String printTree(Term term) {
        return printTreeAux(term).toString();
    }
    private static StringBuilder sb_print_tree = new StringBuilder();

    private static StringBuilder printTreeAux(Term term) {
        StringBuilder sb = new StringBuilder(term.getValue());
        if (term.getListChild() != null) {
            for (Iterator it = term.getListChild().iterator(); it.hasNext();) {
                Term subTerm = (Term) it.next();
                sb.append("\n|\n|--");
                sb.append(insertTab(printTreeAux(subTerm), it.hasNext()));
            }
        }
        return sb;
    }

    private static StringBuilder insertTab(StringBuilder sb, boolean lastParameter) {
        int i = sb.indexOf("\n");
        while (i != -1) {
            if (lastParameter) {
                sb.insert(i + 1, "|  ");
            } else {
                sb.insert(i + 1, "   ");
            }
            i = sb.indexOf("\n", i + 2);
        }
        return sb;
    }

    @Override
    public String toString() {
        return printSyntacticSugar(this).toString();
    }

    private static String getRandomElementHashSet(HashSet<String> hash) {
        random = new Random();
        String value = null;
        int i = 0, n = random.nextInt(hash.size());
        //System.out.println(hash + "\t" + n);
        for (Iterator<String> it = hash.iterator(); it.hasNext();) {
            value = it.next();
            if (i == n) {
                break;
            }
            i++;
        }
        return value;
    }

    public boolean isEquationTerm() {
        return getType() == EQUAL || getType() == EQUAL_SYMBOL;
    }

    public boolean isExampleTerm() {
        return isEquationTerm() && !hasVar();
    }

    public boolean isProgramTerm() {
        if (isEquationTerm()) {
            HashSet hashLhs = getChild(0).getHashVars();
            HashSet hashRhs = getChild(1).getHashVars();
            for (Iterator<String> it = hashRhs.iterator(); it.hasNext();) {
                if (!hashLhs.contains(it.next())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isGoalTerm() {
        return !isEquationTerm() && !hasVar();
    }
}