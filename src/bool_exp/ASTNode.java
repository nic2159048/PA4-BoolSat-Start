package bool_exp;

/** class ASTNode --
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * This node type makes up all of the internal nodes of the 
 * Abstract Syntax Tree for the expressions that are parsed
 * by this library. ASTNodes have a type -- representing 
 * the computational operation being performed -- and a
 * specific number of children depending on the type of 
 * operation. See NodeType enum
 * 
 * Identifier nodes will have the name of their identifier in
 * the id string field. 
 */
public class ASTNode {
    /** enum NodeType --
     * Describes the type of an AST node. Nodes for this grammar
     * can either have no children, one child or two children.
     *
     * Terminal Nodes (Leaf) --
     * ID - Represents an Identifier in the expression, only leaf node.
     *
     * Unary Operators --
     * NOT - Represents the logical not (!) operation, negates
     *       the single operand.
     *
     * Binary Operators --
     * AND - The logical AND operator, evaluates to true if the child1
     *       and child2 operands both evaluate to true; otherwise false.
     * OR  - The logical OR operator, evaluates to true if the child1
     *       or child2 operand evaluates to true.
     */
    private enum NodeType {
        AND, OR, NOT, ID ;
    }

    /* The NodeType Enum specifies the type of this ASTNode: it
     * can be an AND, OR, NOT, or ID node, which
     * have 2, 2, 1, and 0 children, respectively. */
    private NodeType type;

    /* Facilitates variable number of children nodes, see methods
     * below to access children */
    public ASTNode child1, child2;

    /* For ID type nodes, this field is the identifier name */
    private String id;
    
    /* Constructor for an arbitrary NodeType ASTNode */
    private ASTNode(NodeType t) {
        this.type = t;
    }
    
    /* Constructor for a unary operation type ASTNode */
    private ASTNode(NodeType t, ASTNode child){
        this.type = t;
        child1 = child;
    }
    
    /* Constructor for a binary operation type ASTNode */
    private ASTNode(NodeType t, ASTNode child1, ASTNode child2){
        this.type = t;
        this.child1 = child1;
        this.child2 = child2;
    }
    
    /* Constructor for an Identifier NodeType ASTNode */
    private ASTNode(NodeType t, String id) {
        this.type = t;
        this.id   = id;
    }

    public static ASTNode createAndNode(ASTNode left, ASTNode right) {
        return new ASTNode(NodeType.AND, left, right);
    }

    public static ASTNode createOrNode(ASTNode left, ASTNode right) {
        return new ASTNode(NodeType.OR, left, right);
    }

    public static ASTNode createNotNode(ASTNode child) {
        return new ASTNode(NodeType.NOT, child);
    }

    public static ASTNode createIdNode(String value) {
        return new ASTNode(NodeType.ID, value);
    }

    public boolean isAnd() {
        return type == NodeType.AND;
    }

    public boolean isOr() {
        return type == NodeType.OR;
    }

    public boolean isNot() {
        return type == NodeType.NOT;
    }

    public boolean isId() {
        return type == NodeType.ID;
    }

    /* Gets the NodeType attribute */
    public String getNodeType() {
        return this.type.name();
    }
    
    /* Return the name of the identifier this node represents */
    public String getId() {
        return this.id;
    }
}

