package com.jiacp;

import com.jiacp.functions.TestMain;
import com.jiacp.funico.Funico;
import com.jiacp.multiobjetivo.Point;
import javafx.util.Pair;
import unalcol.random.Random;
import unalcol.random.integer.IntegerGenerator;
import fplearning.interpreter.Equation;
import unalcol.types.real.array.DoubleArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiacontrerasp on 11/6/15.
 */
public class Main {

    private static int individualsSize = 100;

    private static String[] formulas = {"x**2 - 2*y","x+ x*y"};


    public static void main(final String[] args) throws Exception
    {
        List<Point> individuals = generate(individualsSize);

    }

    private static List<Point> generate(int size){

        List<Point> individuals = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            Point point = new Point();
            point.setId(i);
            point.setX(Random.random());
            point.setY(Random.random());
            point.setFitness(0);
            individuals.add(i,point);
        }

        return individuals;
    }

    private static boolean compare(Point point1,Point point2 ){

        boolean dominate = false;

        List<Double> resultPoint1 = calcFunctions(point1);
        List<Double> resultPoint2 = calcFunctions(point2);

        int formSize = resultPoint1.size();
        boolean let;

        for (int i = 0; i < formSize; i++){



        }


        return dominate;

    }

    private static List<Double> calcFunctions (Point point){

        List<Double> results = new ArrayList<>();

        for (String form : formulas){
            double result = (point.getX()*point.getX()) - (2 * point.getY());
        }


        /*
        //TODO Borrar cuando implemente funciones
         */
        double result1 = (point.getX()*point.getX()) - (2 * point.getY());
        results.add(result1);
        double result2 = (point.getX()) + (point.getY() * point.getY());
        results.add(result2);
        return results;

    }


}
