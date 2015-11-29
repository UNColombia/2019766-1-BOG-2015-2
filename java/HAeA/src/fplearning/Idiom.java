package fplearning;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Camiku
 */
public class Idiom {

    public static final int SPANISH = 2;
    public static final int ENGLISH = 1;
    public static final int NONE = 0;
    private static int idiom = 0;

    public Idiom() {
    }

    public static int getIdiom() {
        return Idiom.ENGLISH;
    }
    
}
