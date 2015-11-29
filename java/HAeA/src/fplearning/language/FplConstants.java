/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Camiku
 */
public interface FplConstants {

    public static final byte ERROR_TOKEN = -2;
    public static final byte FINAL_TOKEN = -1;
    public static final byte INITIAL = 11;
    public static final byte FUNCTOR = 12;
    public static final byte FUNCTOR_SPACE = 13;
    public static final byte VARIABLE = 14;
    public static final byte NATURAL = 15;
    public static final byte EQUAL = 16;
    public static final byte EQUAL_SYMBOL = 17;
    public static final byte OPEN_PARENTHESIS = 18;
    public static final byte CLOSE_PARENTHESIS = 19;
    public static final byte OPEN_BRACKET = 20;
    public static final byte CLOSE_BRACKET = 21;
    public static final byte RIGHT_ARROW_1 = 22;
    public static final byte RIGHT_ARROW_2 = 23;
    public static final byte REWRITE = 24;
    public static final byte SUB_INDEX = 25;
    public static final byte INFINITY = 26;
    public static final byte INFINITY_NODE = 27;
    public static final byte NULL = 28;
    public static final byte COMMA = 29;
    public static final byte TRUE = 30;
    public static final byte FALSE = 31;
    public static final byte SUCCESSOR = 32;
    public static final byte UNDEFINED = 33;
    public static final byte CONSTANT = 34;
    public static final byte CARRIAGE_RETURN = 100;
    public static final byte WHITE_SPACE = 35;
    public static final byte SEMICOLON = 36;
    public static final byte MARK_OF_BOTTOM = 37;
    public static final byte LIST_SEPARATOR = 38;
    public static final byte COMMENT = 39;
    public static final byte END_COMMENT = 40;
    public static final byte WITHOUT_TYPE = 41;
    public static final byte LIST = 41;
    public static final byte EOF = 42;
    public static final char LIST_FUNCTOR = '•';//©º∎
    public static final byte VAR_TERM = 70;
    public static final byte VAR_END = 71;
    public static final byte VAR_ENDF = 72;
    public static final byte VAR_SEPARATOR = 73;
    public static final byte VAR_ARGUMENT = 74;
    public static final byte VAR_PARAMETER = 75;
    public static final byte VAR_HEAD = 76;
    public static final byte VAR_TAIL = 77;
    public static final byte VAR_LIST = 78;
    public static final String[] TABLE_RESERVED_WORD = {"equal", "s"};
    public static final String[] TABLE_CONSTANT_WORD = {"true", "false", "undef"};
    public static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 13); // "Tahoma" "Courier New"
    public static final Color brown1 = new Color(176, 24, 19);
    public static final Color brown2 = new Color(128, 64, 64);
    public static final Color yellow1 = new Color(204, 102, 0);
    public static final Color blue1 = new Color(50, 185, 185);
    public static final Color blue2 = new Color(0, 153, 255);
    public static final Color violet = new Color(160, 32, 240);
    public static final Color purple = new Color(153, 0, 153);//180, 0, 255
    public static final Color green1 = new Color(0, 102, 51);//0, 153, 0
    public static final Color green2 = new Color(0, 153, 0);
    public static final Color lilac = new Color(102, 102, 255);
    public static final Color COLOR_FUNCTOR = blue2;
    public static final Color COLOR_RESERVED = purple;
    public static final Color COLOR_SEPARATOR = Color.BLACK;
    public static final Color COLOR_VARIABLE = green1;
    public static final Color COLOR_CONSTANT = yellow1;
    public static final Color COLOR_COMMENT = lilac;
    public static final Color COLOR_PARENTHESIS = Color.MAGENTA;
    public static final Color COLOR_BRACKET = Color.BLUE;
    public static final Color COLOR_REWRITE = Color.GRAY;
    public static final Color COLOR_PLAIN = Color.RED;
}
