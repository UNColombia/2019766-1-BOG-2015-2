/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class ListTokensTerms {

    private final LinkedList<Token> listTokens;
    private final LinkedList<String> stackTextTerms;

    public ListTokensTerms(LinkedList<Token> listTokens, LinkedList<String> stackTextTerms) {
        this.listTokens = listTokens;
        this.stackTextTerms = stackTextTerms;
    }

    public LinkedList<Token> getListTokens() {
        return listTokens;
    }

    public LinkedList<String> getStackTextTerms() {
        return stackTextTerms;
    }

}
