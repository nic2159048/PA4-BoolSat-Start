package bool_exp;

/** enum Token --
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * The Token class represents all of the valid entities in
 * a logical expression. See Lexer.java for test cases. Variables
 * in the input expression are represented by the ID Token, &&
 * by the AND token etc.
 */
public class Token {
    private static final String
        PAREN_LEFT = "PAREN_LEFT",
        PAREN_RIGHT = "PAREN_RIGHT",
        ID  = "ID: ",
        AND = "AND",
        OR  = "OR",
        NOT = "NOT";

    private final String type; // The type of token that this Token instance is
    private String text;       // For ID tokens, we also store the id name
    private Token(String type){
        this.type = type;
    }

    /* Creating various token instances */
    static Token createLeftParen(){
        return new Token(PAREN_LEFT);
    }
    static Token createRightParen(){
        return new Token(PAREN_RIGHT);
    }
    static Token createAnd(){
        return new Token(AND);
    }
    static Token createOr(){
        return new Token(OR);
    }
    static Token createNot(){
        return new Token(NOT);
    }
    static Token createId(String identifier){
        Token t = new Token(ID);
        t.setText(identifier);
        return t;
    }

    /* Checking token type method */
    boolean isAnd(){
        return type.equals(AND);
    }
    boolean isOr(){
        return type.equals(OR);
    }
    boolean isNot(){
        return type.equals(NOT);
    }
    boolean isId(){
        return type.equals(ID);
    }
    boolean isLeftParen(){
        return type.equals(PAREN_LEFT);
    }
    boolean isRightParen() {
        return type.equals(PAREN_RIGHT);
    }

    /* Getting and setting identifier name for Tokens of type ID */
    private void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }
    
    public String getString(){
        // If this token is an ID return the string "IDENTIFIER: <name>" otherwise
        // just return the type of the Token.
        return (this.type.equals(Token.ID) ? type + text : this.type);
    }
}