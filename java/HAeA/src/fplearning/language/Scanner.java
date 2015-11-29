/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

import java.text.StringCharacterIterator;
import java.util.LinkedList;

/**
 *
 * @author Camiku
 */
public class Scanner implements FplConstants {

    private static final StringCharacterIterator sci = new StringCharacterIterator("");
    private static final LinkedList<Token> listTokens = new LinkedList();
    private static final LinkedList<String> stackTextTerms = new LinkedList();
    private static final StringBuilder text = new StringBuilder();
    private static final StringBuilder stringProccesed = new StringBuilder();
    private static String textToken;

    /**
     *
     */
    public Scanner() {
    }
    /**
     *
     * @param text
     * @return
     */
    private static int ch, state;

    public static ListTokensTerms generateTokens(String source) throws LexicalException {
        sci.setText(source + "×");
        state = INITIAL;
        listTokens.clear();
        stackTextTerms.clear();
        text.delete(0, text.length());
        stringProccesed.delete(0, stringProccesed.length());
        do {
            ch = sci.current();
/*
            if (ch == 13) {
                System.out.println("\u21B5" + "\t" + state);
            } else if (ch == 10) {
                System.out.println("\u21B2" + "\t" + state);
            } else {
                System.out.println((char) ch + "\t" + state);
            }
        */
            switch (ch) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case '_':
                case '$':
                case '-':
                    switch (state) {
                        case INITIAL:
                        case FUNCTOR:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = FUNCTOR;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    switch (state) {
                        case INITIAL:
                        case VARIABLE:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = VARIABLE;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    switch (state) {
                        case INITIAL:
                        case NATURAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = NATURAL;
                            break;
                        case FUNCTOR:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = FUNCTOR;
                            break;
                        case VARIABLE:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = VARIABLE;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '=':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = EQUAL_SYMBOL;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '(':
                    switch (state) {
                        case FUNCTOR:
                        case FUNCTOR_SPACE:
                            //text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = OPEN_PARENTHESIS;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case ')':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = CLOSE_PARENTHESIS;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '[':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = OPEN_BRACKET;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case ']':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = CLOSE_BRACKET;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '|':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = LIST_SEPARATOR;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case ',':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            stringProccesed.append((char) ch);
                            state = COMMA;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '\n':
                case '\r':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            state = SEMICOLON;
                            break;
                        case SEMICOLON:
                        case END_COMMENT:
                            state = SEMICOLON;
                            break;
                        case COMMENT:
                            state = END_COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case ';':
                    switch (state) {
                        case INITIAL:
                            text.append((char) ch);
                            state = SEMICOLON;
                            break;
                        case SEMICOLON:
                        case END_COMMENT:
                            state = SEMICOLON;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '%':
                    switch (state) {
                        case INITIAL:
                        case COMMENT:
                        case SEMICOLON:
                            state = COMMENT;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case ' ':
                case '\t':
                case '\f':
                case '\b':
                    switch (state) {
                        case INITIAL:
                        case WHITE_SPACE:
                            stringProccesed.append((char) ch);
                            state = WHITE_SPACE;
                            break;
                        case FUNCTOR:
                        case FUNCTOR_SPACE:
                            stringProccesed.append((char) ch);
                            state = FUNCTOR_SPACE;
                            break;
                        case COMMENT:
                            state = COMMENT;
                            break;
                        case SEMICOLON:
                            state = SEMICOLON;
                            break;
                        default:
                            processToken(listTokens, text, state);
                            state = INITIAL;
                            sci.previous();
                            break;
                    }
                    break;
                case '×':
                    processToken(listTokens, text, state);
                    state = EOF;
                    processToken(listTokens, text, state);
                    break;
                default:
                    System.out.println("State by default: " + state);
                    if (state != COMMENT) {
                        do {
                            stringProccesed.append((char) ch);
                            sci.next();
                            ch = sci.current();
                        } while (ch != '\n' && ch != ';' && ch != '×');
                        throw new LexicalException(stringProccesed.toString());
                    }
                    break;
            }
            sci.next();
        } while (ch != '×');
        ListTokensTerms listTokensTerms = new ListTokensTerms(listTokens, stackTextTerms);
        return listTokensTerms;
    }

    private static void processToken(LinkedList<Token> listTokens, StringBuilder text, int state) {
        textToken = text.toString();
        switch (state) {
            case OPEN_PARENTHESIS:
                if (textToken.equals("s")) {
                    listTokens.add(new Token(textToken, SUCCESSOR));
                } else if (textToken.equals("equal")) {
                    listTokens.add(new Token(textToken, EQUAL));
                } else {
                    listTokens.add(new Token(textToken, FUNCTOR));
                }
                listTokens.add(new Token("(", OPEN_PARENTHESIS));
                break;
            case CLOSE_PARENTHESIS:
                listTokens.add(new Token(textToken, CLOSE_PARENTHESIS));
                break;
            case FUNCTOR:
            case FUNCTOR_SPACE:
                if (textToken.equals("true")) {
                    listTokens.add(new Token(textToken, TRUE));
                } else if (textToken.equals("false")) {
                    listTokens.add(new Token(textToken, FALSE));
                } else if (textToken.equals("undef")) {
                    listTokens.add(new Token(textToken, UNDEFINED));
                } else {
                    listTokens.add(new Token(textToken, CONSTANT));
                }
                break;
            case VARIABLE:
                listTokens.add(new Token(textToken, VARIABLE));
                break;
            case NATURAL:
                listTokens.add(new Token(textToken, NATURAL));
                break;
            case EQUAL_SYMBOL:
                listTokens.add(new Token(textToken, EQUAL_SYMBOL));
                break;
            case OPEN_BRACKET:
                listTokens.add(new Token(textToken, OPEN_BRACKET));
                break;
            case CLOSE_BRACKET:
                listTokens.add(new Token(textToken, CLOSE_BRACKET));
                break;
            case LIST_SEPARATOR:
                listTokens.add(new Token(textToken, LIST_SEPARATOR));
                break;
            case COMMA:
                listTokens.add(new Token(textToken, COMMA));
                break;
            case SEMICOLON:
            case END_COMMENT:
                listTokens.add(new Token(";", SEMICOLON));
                if (stringProccesed.length() != 0) {
                    stackTextTerms.add(stringProccesed.toString());
                }
                stackTextTerms.add(";");
                stringProccesed.delete(0, stringProccesed.length());
                break;
            case EOF:
                listTokens.add(new Token("×", MARK_OF_BOTTOM));
                if (stringProccesed.length() != 0) {
                    stackTextTerms.add(stringProccesed.toString());
                }
                stackTextTerms.add("×");
                break;
        }
        text.delete(0, text.length());
    }
}
