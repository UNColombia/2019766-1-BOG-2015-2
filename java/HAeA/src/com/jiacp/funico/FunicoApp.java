package com.jiacp.funico;

import com.jiacp.funico.genetic.Algorithm;
import com.jiacp.funico.genetic.FitnessCalc;
import com.jiacp.funico.genetic.Population;
import fplearning.interpreter.*;
import fplearning.language.LexicalException;
import fplearning.language.SyntacticalException;
import unalcol.evolution.Individual;
import unalcol.evolution.evolution.ga.GeneticAlgorithm;
import unalcol.optimization.replacement.Replacement;
import unalcol.optimization.selection.Selection;
import unalcol.optimization.solution.Solution;
import unalcol.types.collection.vector.Vector;

/**
 * The main class of the application.
 */
public class FunicoApp {

    public FunicoApp() {
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        try {

            int items = 4;
            int knapsack = 8;

            // k = items; g = knapsack;

            /*
            f(k,g) =
                f(k-1,g)                                si w_k > g
                max {v_k + f(k-1,g-w_k), f(k-1,g)}      si w_k <= g y k>0
             */
            /*
            Item i | Value vi | Weight wi
             1          15      1
             2          10      5
             3          9       3
             4          5       4
            */

            String[] funcion = {"knapsack","s"};
            int[] arityFun = {2,1};
            String[] terminal = {"0","X","Y","true","false"};
            int poblacionTam = 100;


            Population poblacion = new Population(poblacionTam,true);



            String programaMax = "" +
                    "greater(0,Y) = false; " +
                    "greater(X,0) = true; " +
                    "greater(s(X),s(Y)) = greater(X,Y);" +
                    "g1(X,Y,true) = X;" +
                    "g1(X,Y,false) = Y;" +
                    "max(X,Y) =  g1(X,Y,greater(X,Y));";
            String programaRest = ""+
                    "rest(0,Y) = Y;"+
                    "rest(X,0) = X;"+
                    "rest(s(X),s(Y)) = rest(X,Y)";

            System.out.println("Probando MAX");
            System.out.println(Evaluator.evalue(programaMax,"max(10,5)"));
            System.out.println(Evaluator.evalue(programaMax,"max(4,4)"));
            System.out.println(Evaluator.evalue(programaMax,"max(0,1)"));
            System.out.println(Evaluator.evalue(programaMax,"max(1,0)"));
            System.out.println(Evaluator.evalue(programaMax,"max(1,15)"));
            System.out.println("##############");

            System.out.println("Probando Rest");
            System.out.println(Evaluator.evalue(programaRest,"rest(10,5)"));
            System.out.println(Evaluator.evalue(programaRest,"rest(4,4)"));
            System.out.println(Evaluator.evalue(programaRest,"rest(0,1)"));
            System.out.println(Evaluator.evalue(programaRest,"rest(1,0)"));
            System.out.println(Evaluator.evalue(programaRest,"rest(1,15)"));
            System.out.println("##############");


            String programaKnapsack = "" +
                    "knapsack(0,g) = 0; " +
                    "knapsack(k,0) = 0; " +
                    //"knapsack(s(k),g) = knapsack(k,g);" +
                    "g2(s(k),g,false) = knapsack(k,g);"+
                    "g2(s(k),g,true) = max(k+knapsack(k,rest(g,k)),knapsack(k,g));"+
                    "knapsack(k,g) = g2(k,g,g1(k,g));";

            String objetivos = "knapsack(4,8)";

            String[][] ejemplos = {
                    {"knapsack(0,1)", "0"},
                    {"knapsack(2,1)", "15"},
                    {"knapsack(3,3)", "15"},
                    {"knapsack(4,5)", "24"},
                    {"knapsack(4,7)", "25"},
                    {"knapsack(3,3)", "true"}
            };

            System.out.println(Evaluator.evalue(programaMax+programaRest+programaKnapsack,objetivos));

            // Evolve our population until we reach an optimum solution
            int generationCount = 0;
            while (poblacion.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
                generationCount++;
                System.out.println("Generation: " + generationCount + " Fittest: " + poblacion.getFittest().getFitness());
                poblacion = Algorithm.evolvePopulation(poblacion);
            }
            System.out.println("Mejor solucion!");
            System.out.println("Generacion: " + generationCount);
            System.out.println("solucion:");
            System.out.println(poblacion.getFittest());


            try {
                Equation equacion = new Equation(programaMax+programaKnapsack);
            } catch (ExampleException e) {
                e.printStackTrace();
            }

            System.out.println(Evaluator.evalue(programaKnapsack,objetivos));




            /*

            System.out.println(Evaluator.evalue(
                    "sum(0,X) = X; sum(s(X),Y) = s(sum(X,Y))",
                    "sum(5,3)"));


            System.out.println(Evaluator.evalue(
                    "mod3(0) = 0; mod3(1) = 1; mod3(2) = 2; mod3(s(s(s(X)))) = mod3(X)",
                    "mod3(5)"));


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


            System.out.println(Evaluator.evalue(
                    "mod3(0) = 0; mod3(1) = 1; mod3(2) = 2; mod3(s(s(s(X)))) = mod3(X)",
                    "mod3(5)"));
            System.out.println(Evaluator.evalue(
                    "even(0) = true; even(1) = false; even(s(s(X))) = even(X)",
                    "even(5)"));
            System.out.println(Evaluator.evalue(
                    "sum(0,X) = X; sum(s(X),Y) = s(sum(X,Y))",
                    "sum(5,3)"));
            
            String[] functor = {"geq", "s"};
            int[] arityFun = {2, 1};
            String[] terminal = {"0", "X", "Y"};
            */
        } catch (ProgramException | GoalException | LexicalException | SyntacticalException ex) {
            System.out.println(ex);
        }
    }
}