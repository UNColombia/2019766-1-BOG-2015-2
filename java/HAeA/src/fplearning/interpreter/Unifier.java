/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

import fplearning.language.FplConstants;
import fplearning.language.Term;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Unifier implements FplConstants {

    public static Replacement getMGU(Term s, Term t) {
        LinkedList<Term> listTermS = new LinkedList();
        LinkedList<Term> listTermT = new LinkedList();
        listTermS.add((Term) s.clone());
        listTermT.add((Term) t.clone());
        return getMGU(listTermS, listTermT);
    }

    private static Replacement getMGU(LinkedList<Term> listTermS, LinkedList<Term> listTermT) {
        Replacement replacement = new Replacement();
        Term s, t;
        do {
            s = listTermS.pop();
            t = listTermT.pop();
            if (s.equals(t)) {
                continue;
            } else {
                switch (s.getType()) {
                    case FUNCTOR:
                    case SUCCESSOR:
                    case LIST:
                    case EQUAL:
                        switch (t.getType()) {
                            case FUNCTOR:
                            case SUCCESSOR:
                            case LIST:
                            case EQUAL:
                                if (s.getType() == t.getType()
                                        && s.getArity() == t.getArity()
                                        && s.getValue().equals(t.getValue())) {
                                    for (int i = s.getArity() - 1; i >= 0; i--) {
                                        listTermS.push(s.getListChild().get(i));
                                        listTermT.push(t.getListChild().get(i));
                                    }
                                    continue;
                                } else {
                                    return null;
                                }
                            case VARIABLE:
                                listTermS.push(t);
                                listTermT.push(s);
                                break;
                            case NULL:
                            case NATURAL:
                            case TRUE:
                            case FALSE:
                            case CONSTANT:
                            case UNDEFINED:
                                return null;
                        }
                        break;
                    case VARIABLE:
                        if (s.isVariableInTerm(t)) {
                            return null;
                        } else {
                            Term termS, termT;
                            for (int i = 0; i < listTermS.size(); i++) {
                                termS = (Term) listTermS.get(i);
                                termT = (Term) listTermT.get(i);
                                termS = Replacement.apply(termS, s.getValue(), t);
                                termT = Replacement.apply(termT, s.getValue(), t);
                                if (termS.isVariableInTerm(s) || termT.isVariableInTerm(s)) {
                                    return null;
                                } else {
                                    listTermS.set(i, termS);
                                    listTermT.set(i, termT);
                                }
                            }
                            replacement.compose(s.getValue(), t);
                        }
                        break;
                    case FALSE:
                    case NULL:
                    case NATURAL:
                    case TRUE:
                    case CONSTANT:
                    case UNDEFINED:
                        if (t.getType() == VARIABLE) {
                            listTermS.push(t);
                            listTermT.push(s);
                        } else {
                            return null;
                        }
                        break;
                }
            }
        } while (!listTermS.isEmpty());
        return replacement;
    }

    public static Replacement getGroundUnifier(Term t, Term ground) {
        if (t.getType() == ground.getType()
                && t.getArity() == ground.getArity()
                && t.getValue().equals(ground.getValue())) {
            LinkedList<Term> listTermT = new LinkedList();
            LinkedList<Term> listTermGround = new LinkedList();
            for (int i = 0; i < t.getArity(); i++) {
                listTermT.add(t.getListChild().get(i));
                listTermGround.add(ground.getListChild().get(i));
            }
            return getGroundUnifier(listTermT, listTermGround);
        } else {
            return null;
        }
    }

    private static Replacement getGroundUnifier(LinkedList<Term> listTermT, LinkedList<Term> listTermGround) {
        Replacement replacement = new Replacement();
        Term ground, t;
        do {
            t = listTermT.pop();
            ground = listTermGround.pop();
            switch (t.getType()) {
                case FUNCTOR:
                case SUCCESSOR:
                case LIST:
                case EQUAL:
                    switch (ground.getType()) {
                        case FUNCTOR:
                        case SUCCESSOR:
                        case LIST:
                        case EQUAL:
                            if (t.getArity() == ground.getArity() && t.getValue().equals(ground.getValue())) {
                                for (int i = t.getArity() - 1; i >= 0; i--) {
                                    listTermT.push(t.getListChild().get(i));
                                    listTermGround.push(ground.getListChild().get(i));
                                }
                                continue;
                            } else {
                                return null;
                            }
                        case VARIABLE:
                        case NULL:
                        case NATURAL:
                        case TRUE:
                        case FALSE:
                        case CONSTANT:
                        case UNDEFINED:
                            return null;
                    }
                    break;
                case VARIABLE:
                    Term termT;
                    String var = t.getValue();
                    for (int i = 0; i < listTermT.size(); i++) {
                        termT = listTermT.get(i);
                        termT = Replacement.apply(termT, var, ground);
                        listTermT.set(i, termT);
                    }
                    replacement.compose(var, ground);
                    break;
                case NULL:
                case NATURAL:
                case TRUE:
                case FALSE:
                case CONSTANT:
                case UNDEFINED:
                    if (t.getValue().equals(ground.getValue())) {
                        continue;
                    } else {
                        return null;
                    }
            }
        } while (!listTermGround.isEmpty());
        return replacement;
    }
}
