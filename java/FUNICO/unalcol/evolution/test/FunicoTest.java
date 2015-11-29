import fplearning.FunicoApp;
import fplearning.interpreter.Evaluator;
import fplearning.interpreter.GoalException;
import fplearning.interpreter.ProgramException;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import funico.EquationSystem;
import funico.EquationSystemFitness;
import funico.EquationsSpace;
import funico.mutation.OperatorSwapMutation;
import funico.mutation.SuccessorMutation;
import funico.xover.EquationSwap;
import funico.xover.EquationXOver;
import unalcol.evolution.haea.HAEA;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.population.variation.ArityTwo;
import unalcol.search.population.variation.Operator;
import unalcol.search.selection.Elitism;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.space.ArityOne;
import unalcol.search.space.Space;
import unalcol.types.collection.vector.Vector;

public class FunicoTest {
    public static void main(String[] args) {
        String[][] examples = {
                {"geq(0,1)", "false"},
                {"geq(0,0)", "true"},
                {"geq(1,0)", "true"},
                {"geq(1,1)", "true"},
                {"geq(1,2)", "false"},
                {"geq(2,1)", "true"},
                {"geq(2,5)", "false"},
                {"geq(5,2)", "true"},
                {"geq(3,3)", "true"}
        };

        String[][] examples1 = {
                {"geq(1,1)", "2"},
                {"geq(3,5)", "8"},
                {"geq(10, 1)", "11"},
                {"geq(1,2)", "3"},
                {"geq(18,2)", "20"},
                {"geq(3,8)", "11"},
                {"geq(7,5)", "12"},
                {"geq(12,2)", "14"},
                {"geq(3,3)", "6"}
        };

        String[] functor = {"geq", "s"};
        int[] arityFun = {2, 1};
        String[] variables = {"X", "Y", "Z", "A", "B"};
        String[] terminals = {"0", "true", "false"};
        int maxEquations = 3;
        int levels = 4;



        Space<EquationSystem> space = new EquationsSpace(maxEquations, examples, variables, functor,
                                                         arityFun, terminals, levels);

        // Optimization function
        OptimizationFunction<EquationSystem> function = new EquationSystemFitness(examples);
        Goal<EquationSystem> goal = new OptimizationGoal<>(function, false, 1.0);

        ArityOne<EquationSystem> sm = new SuccessorMutation();
        ArityOne<EquationSystem> osm = new OperatorSwapMutation();
        ArityTwo<EquationSystem> exo = new EquationXOver();
        ArityOne<EquationSystem> es = new EquationSwap();

        @SuppressWarnings("unchecked")
        Operator<EquationSystem>[] opers = (Operator<EquationSystem>[])new Operator[3];
        opers[0] = sm;
        opers[1] = osm;
        opers[2] = exo;
        // opers[3] = es;

        int POPSIZE = 10;
        int MAXITERS = 300;

        HaeaOperators<EquationSystem> operators = new SimpleHaeaOperators<>(opers);

        Selection<EquationSystem> tournament = new Tournament<>(4);
        Selection<EquationSystem> elitism = new Elitism<>(.9, .1);

        HAEA<EquationSystem> search = new HAEA<EquationSystem>(POPSIZE, operators, tournament, MAXITERS );

        Solution<EquationSystem> solution = search.apply(space, goal);

        System.out.println(solution.quality());
        System.out.println(solution.value());

    }
}
