package fplearning.interpreter;

import fplearning.Idiom;
import fplearning.language.FplConstants;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import fplearning.language.Term;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Camiku
 */
public class Evaluator implements FplConstants {

    public static final int INITIAL_MAX_STEP = 500;
    public static final int WITHOUT_LIMITID_REDEX = -1;
    public static final int GOAL_EVALUATED = 1000;
    public static final int GOAL_NOT_EVALUATED = 1001;
    public static final int INFINITY_STEPS = 1002;
    public static final int NON_TRACE = 2000;
    public static final int TRACE_WITH_SYNTACTICAL_SUGAR = 2001;
    public static final int TRACE_WITHOUT_SYNTACTICAL_SUGAR = 2002;
    private static LinkedList<Boolean> equaEvaluated = new LinkedList<Boolean>();
    private static int status = Evaluator.GOAL_NOT_EVALUATED;
    private static final LinkedList<Term> listGoals = new LinkedList();
    private static Equation equation, equafiltre;
    private static Term lhs, rhs, redex, termfiltre, newGoal, rewGoal;
    private static int maxNumberStep = INITIAL_MAX_STEP;
    private static int numberSteps;
    private static int maxNumberNode;
    private static int numberNodes;
    private static int i_filtre;
    private static int iter_list_equa;
    private static final String textStatusSpanish = " Número de pasos de reescritura: ";
    private static final String textStatusEnglish = " Number of rewrite steps: ";
    private static final StringBuilder sb = new StringBuilder();
    private static boolean hadComputation;
    private static Replacement sigma;

    public Evaluator() {
    }

    /**
     *
     * @param source
     * @param goal
     * @param maxStep
     * @return
     * @throws LexicalException
     * @throws SyntacticalException
     * @throws fplearning.interpreter.ProgramException
     * @throws fplearning.interpreter.GoalException
     */
    public static String evalue(String source, String goal, int maxStep)
            throws LexicalException, SyntacticalException, ProgramException, GoalException {
        Program p = new Program(source);
        Term g = new Goal(goal).getListGoals().getFirst();
        return evalue(p, g, maxNumberStep, Evaluator.WITHOUT_LIMITID_REDEX, Evaluator.NON_TRACE, null, null).toString();
    }

    /**
     *
     * @param program
     * @param goal
     * @param maxStep
     * @param maxNode
     * @return
     */
    public static Term evalue(Program program, Term goal, int maxStep, int maxNode) {
        return evalue(program, goal, maxStep, maxNode, Evaluator.NON_TRACE, null, null);
    }

    public static String evalue(String source, String goal) throws LexicalException, SyntacticalException, ProgramException, GoalException {
        return evalue(source, goal, INITIAL_MAX_STEP);
    }

    /**
     *
     * @param program
     * @param goal
     * @param maxStep
     * @return
     */
    public static Term evalue(Program program, Term goal, int maxStep) {
        return evalue(program, goal, maxStep, Evaluator.WITHOUT_LIMITID_REDEX, Evaluator.NON_TRACE, null, null);
    }

    public static Term evalue(Program program, Term goal, int maxStep,
            int traceState, JTextArea jTextArea, JLabel statusBar) {
        return evalue(program, goal, maxStep, Evaluator.WITHOUT_LIMITID_REDEX, traceState, jTextArea, statusBar);
    }

    public static Term evalue(Program program, Term goal, int maxStep, int maxNode,
            int traceState, JTextArea jTextArea, JLabel statusBar) {
        setMaxNumberStep(maxStep);
        setMaxNumberNode(maxNode);
        listGoals.clear();
        newGoal = (Term) goal.clone();
        listGoals.add(newGoal);
        fillFalseList(program);
        if (traceState > Evaluator.NON_TRACE && jTextArea != null) {
            jTextArea.append("-------------------------------------------------------------------------------------------");
        }
        numberSteps = -1;
        Evaluator.setStatus(Evaluator.GOAL_NOT_EVALUATED);
        do {
            hadComputation = rewriteStep(program.getListEquations(), listGoals, traceState, jTextArea);
            if (hadComputation) {
                Evaluator.setStatus(Evaluator.GOAL_EVALUATED);
            }
            numberSteps++;
            //System.out.println("##### " + numberSteps);
            if (statusBar != null) {
                sb.delete(0, sb.length());
                if (Idiom.getIdiom() == Idiom.ENGLISH) {
                    sb.append("Goal: ");
                    sb.append(goal);
                    sb.append("  ‖  ");
                    sb.append(textStatusEnglish);
                } else {
                    sb.append("Objetivo: ");
                    sb.append(goal);
                    sb.append("  ‖  ");
                    sb.append(textStatusSpanish);
                }
                sb.append(numberSteps);
                statusBar.setText(sb.toString());
            }
        } while (hadComputation && numberSteps < getMaxNumberStep());
        /*
         if(Evaluator.getStatus() == Evaluator.GOAL_NOT_EVALUATED){
         return new Term("undef", UNDEFINED);
         }
         */
        if (jTextArea != null && traceState != Evaluator.NON_TRACE) {
            jTextArea.append("\n");
            if (traceState == Evaluator.TRACE_WITH_SYNTACTICAL_SUGAR) {
                jTextArea.append(listGoals.getFirst().printSyntacticSugar());
            } else {
                jTextArea.append(listGoals.getFirst().print());
            }
            if (Evaluator.getStatus() == Evaluator.INFINITY_STEPS) {
                if (Idiom.getIdiom() == Idiom.SPANISH) {
                    jTextArea.append("\n》》》 El evaluador ha superado el número máximo de pasos de reescritura o de busqueda de redexes!!!");
                } else {
                    jTextArea.append("\n》》》 The evaluator has exceeded the maximum number of steps of rewriting or finding redexes!!!");
                }
            }
            jTextArea.append("\n-------------------------------------------------------------------------------------------\n");
        }
        return listGoals.getFirst();
    }

    private static boolean rewriteStep(LinkedList<Equation> listEquation,
            LinkedList<Term> listGoals, int traceState, JTextArea jTextArea) {
        rewGoal = listGoals.pop();
        //System.err.println(rewGoal);
        RedexIterator.reset(rewGoal);
        do {
            redex = RedexIterator.next();
            //System.out.println("## " + redex);
            if (redex != null) {
                for (iter_list_equa = 0; iter_list_equa < listEquation.size(); iter_list_equa++) {
                    equation = listEquation.get(iter_list_equa);
                    lhs = equation.getLhs();
                    rhs = equation.getRhs();
                    sigma = Unifier.getGroundUnifier(lhs, redex);
                    if (sigma != null) {
                        if (traceState == Evaluator.TRACE_WITH_SYNTACTICAL_SUGAR && jTextArea != null) {
                            jTextArea.append("\n" + rewGoal.printSyntacticSugar()
                                    + " -->₁ 《" + redex.printSyntacticSugar() + "; "
                                    + equation + "; " + sigma.printSyntacticalSugar() + "》");//〈〚《╠〈〉╣》〛〉
                        } else if (traceState == Evaluator.TRACE_WITHOUT_SYNTACTICAL_SUGAR && jTextArea != null) {
                            jTextArea.append("\n" + rewGoal.print()
                                    + " -->₁ 《" + redex.print() + "; "
                                    + equation + "; " + sigma.print() + "》");//〈〚《╠〈〉╣》〛〉ƒ ₍₀₁₂₃₄₅₆₇₈₉₎⁽⁰¹²³⁴⁵⁶⁷⁸⁹⁾
                        }
                        Term t = Term.replaceTerm(rewGoal, redex, rhs, sigma);
                        listGoals.add(t);
                        Evaluator.equaEvaluated.set(iter_list_equa, true);
                        return true;
                    }
                }
            }
        } while (RedexIterator.hasNext() && redex != null);
        listGoals.push(rewGoal);
        return false;
    }

    public static LinkedList<Boolean> getEquaEvaluated() {
        return equaEvaluated;
    }

    public static void setEquaEvaluated(LinkedList<Boolean> equaEvaluated) {
        Evaluator.equaEvaluated = equaEvaluated;
    }

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int status) {
        Evaluator.status = status;
    }

    public static void stopComputing() {
        Evaluator.numberSteps = Integer.MAX_VALUE;
    }

    public static Program filtrateProgram(Program p, Example positive) {
        boolean[] array = new boolean[p.getListEquations().size()];
        System.out.println(p);
        for (Iterator<Equation> it = positive.getListEquations().iterator(); it.hasNext();) {
            equafiltre = it.next();
            termfiltre = Evaluator.evalue(p, equafiltre.getLhs(), Evaluator.getMaxNumberStep(), Evaluator.getMaxNumberNode());
            if (Evaluator.getStatus() == Evaluator.GOAL_EVALUATED && termfiltre.equals(equafiltre.getRhs())) {
                for (i_filtre = 0; i_filtre < array.length; i_filtre++) {
                    array[i_filtre] = array[i_filtre] || Evaluator.getEquaEvaluated().get(i_filtre);
                }
            }
        }
        for (i_filtre = array.length - 1; i_filtre >= 0 && p.getListEquations().size() > 1; i_filtre--) {
            if (!array[i_filtre]) {
                System.out.println(p.removeEquation(i_filtre));
            }
        }
        return p;
    }

    private static void fillFalseList(Program program) {
        Evaluator.status = Evaluator.GOAL_NOT_EVALUATED;
        if (Evaluator.equaEvaluated.size() > program.getListEquations().size()) {
            for (i_filtre = Evaluator.equaEvaluated.size(); i_filtre > program.getListEquations().size(); i_filtre--) {
                Evaluator.equaEvaluated.removeLast();
            }
        } else if (Evaluator.equaEvaluated.size() < program.getListEquations().size()) {
            for (i_filtre = Evaluator.equaEvaluated.size(); i_filtre < program.getListEquations().size(); i_filtre++) {
                Evaluator.equaEvaluated.add(false);
            }
        }
        for (i_filtre = 0; i_filtre < Evaluator.equaEvaluated.size(); i_filtre++) {
            Evaluator.equaEvaluated.set(i_filtre, false);
        }
    }

    public static int getMaxNumberStep() {
        return maxNumberStep;
    }

    public static void setMaxNumberStep(int maxNumberStep) {
        Evaluator.maxNumberStep = maxNumberStep;
    }

    public static int getNumberSteps() {
        return numberSteps;
    }

    public static void setNumberSteps(int numberSteps) {
        Evaluator.numberSteps = numberSteps;
    }

    public static int getMaxNumberNode() {
        return maxNumberNode;
    }

    public static void setMaxNumberNode(int maxNumberNode) {
        Evaluator.maxNumberNode = maxNumberNode;
    }

    public static int getNumberNodes() {
        return numberNodes;
    }

    public static void setNumberNodes(int numberNodes) {
        Evaluator.numberNodes = numberNodes;
    }
}
