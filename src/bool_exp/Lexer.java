package bool_exp;

import java.util.ArrayList;
import java.util.List;

/** class Lexer --
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * The Lexer class models a Finite State Automaton (Machine)
 * that accepts the language of boolean logical expressions. This class
 * produces Tokens (Java objects representing sequences of text from the
 * input) as the input string is fed into the next() function one
 * character at a time. The next() function will return control codes
 * telling the caller that should pass the next character from the input
 * the next time next() is called ("CONSUME") or that the current character
 * should be passed into next() again ("LEAVE").
 *
 * Legend:
 * (C)       - Consume the input character
 * (L)       - Leave the input character
 * -> XXX    - Transition to State XXX
 * (P - TTT) - Produce Token TTT and enter FINAL state
 * ERROR     - The specified input at this state produces an error
 *
 * The State Transitions are as follows:
 *                                                     Input Characters
 *              ----------------------------------------------------------------------------------------------
 * State Column | isAlpha() |        '('        |        ')'         |     '!'     |     '&'     |    '|'    |
 * -----------------------------------------------------------------------------------------------------------
 *    START     | (C) -> ID | (C)P - PAREN_LEFT | (C)P - PAREN_RIGHT | (C)P - NOT  | (C) -> AND  | (C) -> OR |
 *     AND      |  ERROR    |        ERROR      |       ERROR        |    ERROR    | (C)P - AND  |   ERROR   |
 *      OR      |  ERROR    |        ERROR      |       ERROR        |    ERROR    |    ERROR    | (C)P - OR |
 *      ID      | (C) -> ID |     (L)P - ID     |    (L)P - ID       | (L)P - ID   | (L)P - ID   | (L)P - ID |
 * -----------------------------------------------------------------------------------------------------------
 */
public class Lexer {

    /** enum State --
     *
     * The state enum describes both the internal machine state of the
     * Lexer Finite State Machine, and describes the output
     * control codes that the machine returns at each iteration of the
     * state simulation.
     */
    public enum State {
        /* INTERNAL MACHINE STATES */
        START,   // Before any input has been processed, or immediately after a
        // Token has been returned, the machine will be in the START state

        FINAL,   // The machine enters the FINAL state when a Token has been parsed

        ID,      // While more characters that belong to an indentifier are being
        // received, the machine will remain in the ID state

        AND,     // Similar to EQ, enter this state after the first '&' and either
        // return the AND operator Token if another is encountered or fail

        OR,      // Similar to EQ, enter this state after the first '|' and either
        // return the OR operator Token if another is encountered or fail

        INVALID, // An invalid state is one where there is no supported transition
        // from the current state given the next input.

        /* CONTROL STATES RETURNED TO CALLER */
        CONSUME_DONE, // A Token has been produced, consume the character that was
        // used in the Lexer
        LEAVE_DONE,   // A Token has been produced, do NOT consume that character
        CONSUME,      // No token was produced, consume the input character
        LEAVE;        // No token was produced, do not consume the character
    }

    /* For identifiers, we want to gradually build the identifier
     * name as it is gradually being passed in letter by letter */
    private StringBuilder text = new StringBuilder();

    /* List of tokens that the Lexer was able to produce from the
     * input expression */
    private ArrayList<Token> tokens = new ArrayList<>();

    private State state = State.START; // The state that the machine is in.
    private Token result;              // The token that was most recently produced

    private int numStates = 0;         // The number of state transitions that have been done

    public Lexer(String input){
        // Add a space to the end of input so as to not drop tokens that
        // need another input after the current state to return the token.
        input = input + " ";

        // Go through each character of the input and feed each character
        // as input to the finite state machine. Produce a list of tokens
        // that the machine generates.
        for(int i = 0; i < input.length(); /* NOP */){

            // Depending on the resting state of the transition table, process
            // the output appropriately
            switch(next(input.charAt(i))){
                case CONSUME_DONE:
                    i++;
                    tokens.add(getToken());
                    break;
                case CONSUME:
                    i++;
                    break;
                case LEAVE_DONE:
                    tokens.add(getToken());
                    break;
                case LEAVE: /* DROP THROUGH */
                default:
                    break;
            }
        }
    }

    /** getToken() --
     * Reset the state of the machine and return the token that was produced.
     * Reports an error and returns null if this method was called improperly.
     * @return The last token that the lexer produced.
     */
    private Token getToken(){
        switch(state) {
            // The machine should be in the FINAL state if a Token was produced
            case FINAL:
                Token tmp = result;
                result = null;
                state = State.START;
                text = new StringBuilder();
                return tmp;

            // This case should not occur with proper operation
            default:
                System.err.println("No token result to fetch");
                return null;
        }
    }

    /* peek(), next(), empty() --
     * The following functions provide a queue interface for the LL(1) Parsers
     * that produce the expression trees from these tokens. Parsers can get the
     * list of tokens and work from that list, or use peek(), next(), empty()
     * and allow the Lexer to provide the tokens.
     */
    private int tokenPosition = 0;
    Token peek(){
        return tokenPosition < tokens.size() ? tokens.get(tokenPosition) : null;
    }

    Token next(){
        return tokenPosition < tokens.size() ? tokens.get(tokenPosition++) : null;
    }

    boolean empty(){
        return !(tokenPosition < tokens.size());
    }
    
    /** next(char c) --
     * The next function advances this State Machine simulation by one step
     * given the next input character from the input expression. 
     * 
     * The next function returns a control state, such as CONSUME_DONE
     * which indicates to the caller that the current character of input
     * that was given to this method can be discarded (CONSUME'ed), and
     * that a Token is ready for the caller to fetch (_DONE). Grabbing the
     * Token (getToken()) will reset the state of the machine to the starting
     * state so that the next input will be used toward the next Token
     *
     * @return The resting state of the machine after the transition due to
     *         the current input character c.
     */
    private State next(char c){
        numStates++;
        switch(state){
            case START:
                /* Looking at first input from start state, determine 
                 * which branch of the machine we want to take. */
                if(Character.isAlphabetic(c) || c == '_' || c == '$'){
                    state = State.ID;
                    text.append(c);
                    return State.CONSUME;
                }
                /* Whitespace can be ignored from the START state */
                else if(Character.isWhitespace(c))
                    return State.CONSUME;
                /* If we see a '!' character from the START state, go to the
                 * NOT state to wait for an '=' or anything else */
                else if(c == '!'){
                    state  = State.FINAL;
                    result = Token.createNot();
                    return State.CONSUME_DONE;
                }
                /* First character of logical AND operator */
                else if(c == '&'){
                    state = State.AND;
                    return State.CONSUME;
                }
                /* First character of logical OR operator */
                else if(c == '|'){
                    state = State.OR;
                    return State.CONSUME;
                }
                /* These transitions go from the START state directly to a FINAL
                 * state and returns a token immediately */
                else if(c == '('){
                    state = State.FINAL;
                    result = Token.createLeftParen();
                    return State.CONSUME_DONE;
                }
                /* These transitions go from the START state directly to a FINAL
                 * state and returns a token immediately */
                else if(c == ')'){
                    state = State.FINAL;
                    result = Token.createRightParen();
                    return State.CONSUME_DONE;
                }
                
                /* The input is not recognized -- error in the input */
                else{
                    error(c);
                    return null;
                }
            
            case ID:
                /* After seeing the start of an identifier, we enter this state.
                 * If the next input is more of the identifier, add to it, otherwise
                 * we need to enter the final state, generate the token, and tell the
                 * caller to not consume the non-identifier character */
                if(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '$'){
                    text.append(c);
                    return State.CONSUME;
                }else{
                    state  = State.FINAL;
                    result = Token.createId(text.toString());
                    return State.LEAVE_DONE;
                }
            
            case AND:
                /* If we find the second '=' character we can return the AND Token */
                if(c == '&'){
                    state  = State.FINAL;
                    result = Token.createAnd();
                    return State.CONSUME_DONE;
                }
                error(c);
                return null;
                
            case OR:
                if(c == '|'){
                    state  = State.FINAL;
                    result = Token.createOr();
                    return State.CONSUME_DONE;
                }
                error(c);
                return null;
                
            case FINAL:
                /* If the user mistakenly passes more input, inform them again that
                 * this input should not be consumed and that a token is waiting
                 * to be collected. */
                return State.LEAVE_DONE;
                
            case INVALID:
            default: 
                error(c);
        }
        /* Should never be reached */
        return null;
    }

    /* Fatal error Tokenizing the input, report the state of the machine and exit */
    private void error(char input){
        System.err.println("Lexer Error at character " + numStates + ": '" + input + "'");
        System.err.println("Current text: '" + text.toString() + "'");
        System.err.println("State History: " + state + " -> " + State.INVALID);
        System.exit(1);
    }

    /* Testing code below this line */
    private static int testNo = 0;
    private static void prettyPrint(List<Token> tokens){
        System.out.print("TestNo " + testNo++ + ": ");
        for(Token t : tokens){
            System.out.print(t.getString() + ", ");
        }
        System.out.println();
    }

    private List<Token> getTokens(){
        return tokens;
    }

    // Testing Routine
    public static void main(String[] args){
        System.out.println("Logic tests");

        prettyPrint(new Lexer("").getTokens());
        prettyPrint(new Lexer("isDone").getTokens());
        prettyPrint(new Lexer("isDone || failed").getTokens());
        prettyPrint(new Lexer("failed && exploded").getTokens());
        prettyPrint(new Lexer("! failed").getTokens());
        prettyPrint(new Lexer("!failed && !exploded").getTokens());
        prettyPrint(new Lexer("(isDone)").getTokens());
        prettyPrint(new Lexer("((((isDone))))").getTokens());
        prettyPrint(new Lexer("((isDone) || (exploded))").getTokens());
    }
}
