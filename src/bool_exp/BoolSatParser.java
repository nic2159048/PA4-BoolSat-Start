package bool_exp;

/** class BoolSatParser --
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * This class implements a Recursive Descent Parser to produce
 * Syntax Trees for boolean logic expressions.
 *
 * This class implements a recursive descent parser for the following
 * Context Free Grammar. Please consider the following notes:
 *      - * indicates the START non-terminal (root of parse)
 *      - non-terminals are any sequence of characters and symbols separated
 *        by whitespace, terminals appear inside single quotes.
 *      - methods at the bottom of this class and the non-terminals in this CFG
 *        are named to correspond with each other for clarity.
 *      - parseA single ' after a non-terminal (e.g. parseA' - which reads "parseA Prime")
 *        corresponds to ..._PRIME methods (for example, parseA_PRIME(...)).
 *
 *            parseE*      -> parseA parseE'
 *            parseE'      -> '||' parseE | ε
 *            parseA       -> parseB parseA'
 *            parseA'      -> '&&' parseA | ε
 *            parseB       -> '!' parseB' | parseB'
 *            parseB'      -> '(' parseE ')' | '<java identifier>'
 */
public class BoolSatParser {
    /* parse(String) --
     * The parse function instantiates a lexer (Lexer object to produce
     * a sequence of Tokens) and returns an Abstract Syntax Tree root node.
     */
    public static ASTNode parse(String expression){
        return parseE(new Lexer(expression));
    }

    /* ------------------------------------
     * Recursive Descent Parsing Functions:
     * ------------------------------------ */
    private static ASTNode parseE(Lexer toks){
        /* If the token sequence is empty, then we have no parsing to do */
        return toks.empty() ? null : parseE_PRIME(toks, parseA(toks));
    }

    private static ASTNode parseE_PRIME(Lexer toks, ASTNode left){
        /* If there are no more tokens, then the sub-expression has been fully
         * parsed and exists fully in the child1 subtree that was passed in.
         *
         * If the token list is not empty (which must be true if the child2 of the OR
         * expression is checked in the following if statement), and starts with
         * any token other than AND then we are in the wrong production rule and
         * should return the child1 subtree for the correct production rule
         * to handle. */
        if(toks.empty() || !toks.peek().isOr())
            return left;

        /* Construct the OR node, and update the token list */
        toks.next();
        /* Note: we need to recursively call parseE_PRIME passing the evaluated
         * node in as the 'child1' param because || is child1 associative. For
         * example, consider "a || b || c". leftAssociativeFix will be the tree:
         *     ||
         *    /  \
         *    a   b
         *
         * Which then needs to be passed back into parseE_PRIME according to the
         * grammar so that it can be the child1 subtree of:
         *          ||
         *         /  \
         *        ||   c
         *       /  \
         *       a   b
         */
        ASTNode leftAssociativeFix = ASTNode.createOrNode(left, parseA(toks));
        return parseE_PRIME(toks, leftAssociativeFix);
    }

    private static ASTNode parseA(Lexer toks){
        return parseA_PRIME(toks, parseB(toks));
    }

    private static ASTNode parseA_PRIME(Lexer toks, ASTNode left){
        /* The comment in this spot in parseE_PRIME() method applies here as well*/
        if(toks.empty() || !toks.peek().isAnd())
            return left;

        /* Construct the AND node, update token list */
        toks.next();
        /* See the note about child1-associativity fixing in this location of the
         * parseE_PRIME() method above, the same fix is being applied here */
        return parseA_PRIME(toks, ASTNode.createAndNode(left, parseB(toks)));
    }

    private static ASTNode parseB(Lexer toks){
        if(!toks.peek().isNot())
            return parseB_PRIME(toks);
        else {
            toks.next(); // remove NOT token
            return ASTNode.createNotNode(parseB_PRIME(toks));
        }
    }

    private static ASTNode parseB_PRIME(Lexer toks){
        if(toks.empty()) {
            System.err.println("Unexpected end of input. Parsing failed.");
            System.exit(1);
        }

        if(toks.peek().isLeftParen()) {
            /* Given a token list: [..,PAREN_LEFT, <EXPRESSION>, PAREN_RIGHT,..]
            * This rule produces a PAREN_GROUP expression with a single child
            * that is the root the of a nested expression tree */
            // remove child1 parenthesis token
            toks.next();

            // Parse the expression subtree
            ASTNode grouping = parseE(toks);

            // If we don't find the child2 paren then the input is malformed
            Token expectedRightParen = toks.next(); //consume the token
            if (!expectedRightParen.isRightParen()) {
                System.err.println("Unclosed parenthesized expression. Unexpected token: "
                        + expectedRightParen + ". Parsing failed.");
                System.exit(1);
            }
            return grouping;
        } else if(toks.peek().isId()) {
            return ASTNode.createIdNode(toks.next().getText());
        } else{
            System.err.println("Unexpected token " + toks.peek() + ". Parsing failed.");
            System.exit(1);
            return null;
        }
    }


    /* dotify(ASTNode) --
     * The dotify function creates and prints to stdout a GraphViz dot
     * representation of the AST nodes. Please use a dot utility such
     * as the website http://sandbox.kidstrythisathome.com/erdos/ or
     * the dot command line tool provided in most linux distributions
     * to construct the visual representation of the dot format.
     */
    public static String dotify(ASTNode root){
        StringBuilder dot = new StringBuilder();
        dot.append("digraph expression_tree {\n");
        dotHelp(dot, root);
        dot.append("}\n");
        return dot.toString();
    }

    /* This function does the recursive traversal of the ASTNodes adding
     * to the dot output at each step of the traversal */
    private static void dotHelp(StringBuilder dot, ASTNode cur){

        /* Base case, if we hit null we have bottomed out */
        if(cur == null)
            return;

        /* Do the work - define the content of the current node in dot format */
        if(cur.isId())
            dot.append(String.format("\t%d [label=\"ID: %s\" shape=box];\n",
                    cur.hashCode(),
                    cur.getId()));
        else
            dot.append(String.format("\t%d [label=\"%s\"];\n",
                    cur.hashCode(),
                    cur.getNodeType()));

        /* recursively define all other children nodes before defining the
         * digraph edge between the current node and its children. */
        if(cur.child1 != null){
            dotHelp(dot, cur.child1);
            dot.append(String.format("\t%d -> %d;\n",
                    cur.hashCode(),
                    cur.child1.hashCode()));
        }
        if(cur.child2 != null) {
            dotHelp(dot, cur.child2);
            dot.append(String.format("\t%d -> %d;\n",
                    cur.hashCode(),
                    cur.child2.hashCode()));
        }
    }

    public static void main(String[] args){
        System.out.println(dotify(parse("")));
        System.out.println(dotify(parse("isDone")));
        System.out.println(dotify(parse("isDone || failed")));
        System.out.println(dotify(parse("failed && exploded")));
        System.out.println(dotify(parse("! failed")));
        System.out.println(dotify(parse("!failed && !exploded")));
        System.out.println(dotify(parse("(isDone)")));
        System.out.println(dotify(parse("((((isDone))))")));
        System.out.println(dotify(parse("((((isDone)) && other))")));
        System.out.println(dotify(parse("((isDone) || (exploded))")));
        System.out.println(dotify(parse("a && b && c")));
        System.out.println(dotify(parse("a || b || c")));
        System.out.println(dotify(parse("a || b && c")));
        System.out.println(dotify(parse("a && b || c")));
        System.out.println(dotify(parse("a && b && c && d || e || f || g && h || i && j")));
        System.out.println(dotify(parse("a && b || !c && d || e && !f")));
        System.out.println(dotify(parse("(var0 || !var1 || !var2) && (var3 || !var2 || !var4)")));
    }
}
