/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

/**
 *
 * @author Camiku-LapTop
 */
public class ProgramException extends Exception {

    private String string;

    public ProgramException(String string) {
        super(string);
        this.string = string;
    }

    @Override
    public String toString() {
        return "ProgramException{" + "equation ≡ " + string + '}';
    }
}
