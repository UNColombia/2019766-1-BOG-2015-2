/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

import fplearning.language.Term;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Replacement implements fplearning.language.FplConstants {
    
    private HashMap<String, Term> table;
    
    public Replacement() {
        table = new HashMap();
    }
    
    public void addLink(String key, Term link) {
        table.put(key, link);
    }
    
    public Term getLink(String key) {
        return table.get(key);
    }
    
    public boolean isVariable(String key) {
        return table.containsKey(key);
    }
    
    public Term apply(Term term) {
        Term newTerm = (Term) term.clone();
        if (this.getTable().isEmpty()) {
            return newTerm;
        } else {
            return replacement(newTerm, this);
        }
    }
    
    private static Term replacement(Term term, Replacement replacement) {
        switch (term.getType()) {
            case VARIABLE:
                if (replacement.isVariable(term.getValue())) {
                    Term parent = term.getParent();
                    if (parent != null) {
                        LinkedList<Term> listChild = parent.getListChild();
                        Term link = (Term) (replacement.getLink(term.getValue()).clone());
                        listChild.set(listChild.indexOf(term), link);
                        link.setParent(parent);
                    } else {
                        term = (Term) (replacement.getLink(term.getValue())).clone();
                    }
                }
                break;
            case FUNCTOR:
            case LIST:
            case SUCCESSOR:
            case EQUAL:
            case EQUAL_SYMBOL:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    replacement(it.next(), replacement);
                }
                break;
            default:
                break;
        }
        return term;
    }
    
    public static Replacement compose(Replacement delta, Replacement sigma) {
        Replacement r = new Replacement();
        if (sigma.getTable().isEmpty()) {
            r.getTable().putAll(delta.getTable());
        } else {
            Term term;
            String key;
            for (Iterator<String> it = sigma.getTable().keySet().iterator(); it.hasNext();) {
                key = it.next();
                term = sigma.getLink(key);
                term = replacement(term, delta);
                if (term.getType() == VARIABLE && term.getValue().equals(key)) {
                } else {
                    r.addLink(key, term);
                }
            }
            for (Iterator<String> it = delta.getTable().keySet().iterator(); it.hasNext();) {
                key = it.next();
                if (!r.isVariable(key)) {
                    r.addLink(key, delta.getLink(key));
                }
            }
        }
        return r;
    }
    
    public void compose(String var, Term link) {
        if (table.isEmpty()) {
            addLink(var, (Term) link.clone());
        } else {
            Term term;
            String key;
            for (Iterator<String> it = table.keySet().iterator(); it.hasNext();) {
                key = it.next();
                term = this.getLink(key);
                term = apply(term, var, link);
                if (term.getType() == VARIABLE && term.getValue().equals(var)) {
                } else {
                    addLink(key, term);
                }
            }
            if (!table.containsKey(var)) {
                addLink(var, link);
            }
        }
    }
    
    public static Term apply(Term term, String var, Term link) {
        return singleReplacement((Term) term.clone(), var, link);
    }
    
    private static Term singleReplacement(Term term, String var, Term link) {
        switch (term.getType()) {
            case VARIABLE:
                if (term.getValue().equals(var)) {
                    if (term.getParent() == null) {
                        return (Term) link.clone();
                    } else {
                        Term copy = (Term) link.clone();
                        copy.setParent(term.getParent());
                        LinkedList list = term.getParent().getListChild();
                        list.set(list.indexOf(term), copy);
                    }
                }
                break;
            case FUNCTOR:
            case LIST:
            case SUCCESSOR:
            case EQUAL:
                for (Iterator<Term> it = term.getListChild().iterator(); it.hasNext();) {
                    singleReplacement(it.next(), var, link);
                }
                break;
            default:
                break;
        }
        return term;
    }
    
    public HashMap getTable() {
        return table;
    }
    
    public void setTable(HashMap table) {
        this.table = table;
    }
    
    public void clear() {
        this.table.clear();
    }
    
    @Override
    public String toString() {
        return table.toString();
    }
    
    public String print() {
        StringBuilder sb = new StringBuilder("{");
        String key;
        for (Iterator<String> it = getTable().keySet().iterator(); it.hasNext();) {
            key = it.next();
            sb.append(key);
            sb.append("=");
            sb.append(table.get(key).print());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public String printSyntacticalSugar() {
        return toString();
    }
}
