package funico;

import fplearning.interpreter.Evaluator;
import fplearning.interpreter.GoalException;
import fplearning.interpreter.ProgramException;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import unalcol.optimization.OptimizationFunction;

public class EquationSystemFitness extends OptimizationFunction<EquationSystem>{
    public EquationSystemFitness(String[][] examples) {
        this.examples = examples;
    }

    @Override
    public Double apply(EquationSystem equationSystem) {
        //System.out.println("fitness start");

        double count = 0.0;
        String es = equationSystem.toString();

        for(int i = 0; i < this.examples.length; ++i) {
            try {
                //System.out.println(es);
                String answer = Evaluator.evalue(es, this.examples[i][0], 500);
                //System.out.println("end");
                if(answer.equals(this.examples[i][1])) count++;
            } catch (ProgramException | GoalException | LexicalException | SyntacticalException ex) {
                System.out.println(ex);
            }
        }

        //System.out.println("fitness end");

        return count / this.examples.length;
    }

    private String[][] examples;
}
