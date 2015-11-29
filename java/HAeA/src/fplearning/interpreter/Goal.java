/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

import fplearning.language.FplConstants;
import fplearning.language.LexicalException;
import fplearning.language.Parser;
import fplearning.language.SyntacticalException;
import fplearning.language.Term;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Goal implements FplConstants {

    private LinkedList<Term> listGoals = null;
    private static Term termGoal;

    public Goal(Goal goal) {
        listGoals = new LinkedList<Term>();
        for (Iterator<Term> it = goal.getListGoals().iterator(); it.hasNext();) {
            listGoals.add((Term) it.next().clone());
        }
    }

    public Goal(Term root) {
        listGoals = new LinkedList<Term>();
        listGoals.add(root);
    }

    public Goal(LinkedList<Term> list) throws GoalException {
        if (list == null) {
            listGoals = null;
        } else {
            listGoals = new LinkedList<Term>();
            for (Iterator<Term> it = list.iterator(); it.hasNext();) {
                termGoal = it.next();
                if (termGoal.isGoalTerm()) {
                    listGoals.add(termGoal);
                } else {
                    throw new GoalException(termGoal.toString());
                }
            }
        }
    }

    public Goal(String textGoal) throws LexicalException, SyntacticalException, GoalException {
        this(Parser.parsing(textGoal));
    }

    /**
     * Get the value of term
     *
     * @return the value of term
     */
    public LinkedList<Term> getListGoals() {
        return listGoals;
    }

    /**
     * Set the value of term
     *
     * @param term new value of term
     */
    public void setListGoal(LinkedList<Term> listGoals) {
        this.listGoals = listGoals;
    }

    @Override
    public Object clone() {
        return new Goal(this);
    }

    @Override
    public String toString() {
        return listGoals.toString();
    }
}
