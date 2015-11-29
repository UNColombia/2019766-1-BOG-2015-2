/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fplearning.language;

/**
 *
 * @author Camiku
 */
public class Token implements FplConstants {

    private String value;
    private byte type;
    
    public Token(Token token) {
        this(token.getValue(), token.getType());
    }

    public Token(String value, byte type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public byte getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Object clone() {
        return new Token(this);
    }

    @Override
    public String toString() {
        return "<" + value + ":" + type + '>';
    }
    
}
