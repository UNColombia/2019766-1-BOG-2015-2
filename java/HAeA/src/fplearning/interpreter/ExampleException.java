/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.interpreter;

/**
 *
 * @author Camiku-LapTop
 */
public class ExampleException extends Exception {

    private String string;

    public ExampleException(String string) {
        super(string);
        this.string = string;
    }

    @Override
    public String toString() {
        /*
         JOptionPane.showMessageDialog(null,
         "Symbol not allowed:"
         + "\n     " + line + "\n          "
         + (char) ch + " . . .\n"
         + "at line number: " + numberline,
         "Lexical error",
         JOptionPane.ERROR_MESSAGE);
         */
        return "ExampleException{" + "source ≡ " + string + '}';
    }
}
