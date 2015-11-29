/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

/**
 *
 * @author Camiku-LapTop
 */
public class SyntacticalException extends Exception {

    private String string;

    public SyntacticalException(String string) {
        super(string);
        this.string = string;
    }

    @Override
    public String toString() {
        /*
         JOptionPane.showMessageDialog(null,
         stringProcessed + "\n     " + token.getValue(),
         "Syntactical error",
         JOptionPane.ERROR_MESSAGE);
         */
        return "SyntacticalException{" + "source ≡ " + string + '}';
    }
}
