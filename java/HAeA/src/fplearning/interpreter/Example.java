/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

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
public class Example {

    private LinkedList<Equation> listEquations = null;
    private int maxLength = 0;
    private static Equation equaExam;
    private static Term termExam;

    public Example() {
        maxLength = 0;
    }

    public Example(Example example) {
        this.listEquations = new LinkedList<Equation>();
        maxLength = 0;
        Equation e;
        for (Iterator<Equation> it = example.getListEquations().iterator(); it.hasNext();) {
            e = (Equation) it.next().clone();
            this.listEquations.add(e);
            maxLength += e.getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
    }

    public Example(Equation e) {
        listEquations = new LinkedList<Equation>();
        listEquations.add(e);
        maxLength = e.getMaxLength();
    }

    public Example(String str) throws LexicalException, SyntacticalException, ExampleException {
        this(Parser.parsing(str));
    }

    public Example(LinkedList<Term> list) throws ExampleException {
        if (list == null) {
            listEquations = null;
        }else{
            listEquations = new LinkedList<Equation>();
            maxLength = 0;
            for (Iterator<Term> it = list.iterator(); it.hasNext();) {
                termExam = it.next();
                if (termExam.isExampleTerm()) {
                    equaExam = new Equation(termExam);
                    this.listEquations.add(equaExam);
                    maxLength += equaExam.getMaxLength();
                    if (it.hasNext()) {
                        maxLength++;
                    }
                } else {
                    throw new ExampleException(termExam.toString());
                }
            }
        }
    }

    public LinkedList<Equation> getListEquations() {
        return listEquations;
    }

    public void setListEquations(LinkedList<Equation> listEquations) {
        this.listEquations = listEquations;
        maxLength = 0;
        for (Iterator<Equation> it = this.listEquations.iterator(); it.hasNext();) {
            maxLength += it.next().getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
    }

    public void addEquationItself(Equation e) {
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
            maxLength = 0;
        } else {
            maxLength++;
        }
        this.listEquations.add(e);
        maxLength += e.getMaxLength();
    }

    public void addEquationItself(LinkedList<Equation> list) {
        Equation e;
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
            maxLength = 0;
        } else {
            maxLength++;
        }
        for (Iterator<Equation> it = list.iterator(); it.hasNext();) {
            e = it.next();
            this.listEquations.add(e);
            maxLength += e.getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
    }

    public void addEquationClone(Equation e) {
        e = (Equation) e.clone();
        if (listEquations == null) {
            listEquations = new LinkedList<Equation>();
            maxLength = 0;
        } else {
            maxLength++;
        }
        listEquations.add(e);
        maxLength += e.getMaxLength();
    }

    public void addEquationClone(LinkedList<Equation> list) {
        Equation e;
        if (this.listEquations == null) {
            this.listEquations = new LinkedList<Equation>();
            maxLength = 0;
        } else {
            maxLength++;
        }
        for (Iterator<Equation> it = list.iterator(); it.hasNext();) {
            e = (Equation) it.next().clone();
            this.listEquations.add(e);
            maxLength += e.getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
    }

    @Override
    public Object clone() {
        return new Example(this);
    }

    @Override
    public String toString() {
        return listEquations.toString();
    }

    public int calculateMaxLength() {
        maxLength = 0;
        for (Iterator<Equation> it = listEquations.iterator(); it.hasNext();) {
            maxLength += it.next().getMaxLength();
            if (it.hasNext()) {
                maxLength++;
            }
        }
        return maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
