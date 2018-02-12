import org.junit.Assert;
import org.junit.Test;

import bool_exp.ASTNode;


public class ASTNodeTest {
    // Tests the functions of the AND node type
	@Test
	public void testAND() {
        // Creates a node using null as children
        ASTNode astAnd = ASTNode.createAndNode(null, null);
        // Asserts to confirm type
        Assert.assertTrue(astAnd.isAnd());
        Assert.assertFalse(astAnd.isId());
        Assert.assertFalse(astAnd.isOr());
        Assert.assertFalse(astAnd.isNot());
        // Node created using a node
        ASTNode astChild = ASTNode.createAndNode(astAnd, astAnd);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("AND"));


	}

    @Test
    public void testOR() {
        // Creates a node using null as children
        ASTNode astOr = ASTNode.createOrNode(null, null);
        // Asserts to confirm type
        Assert.assertTrue(astOr.isOr());
        Assert.assertFalse(astOr.isAnd());
        Assert.assertFalse(astOr.isId());
        Assert.assertFalse(astOr.isNot());
        // Node created using a node
        ASTNode astChild = ASTNode.createOrNode(astOr, astOr);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("OR"));
    }

    @Test
    public void testNot() {
        // Creates a node using null as children
        ASTNode astNot = ASTNode.createNotNode(null);
        // Asserts to confirm type
        Assert.assertTrue(astNot.isNot());
        Assert.assertFalse(astNot.isOr());
        Assert.assertFalse(astNot.isAnd());
        Assert.assertFalse(astNot.isId());
        // Node created using a node
        ASTNode astChild = ASTNode.createNotNode(astNot);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("NOT"));

    }

    @Test
    public void testId() {
        // Creates a node using null as children
        ASTNode astId = ASTNode.createIdNode(null);
        // Asserts to confirm type
        Assert.assertTrue(astId.isId());
        Assert.assertFalse(astId.isOr());
        Assert.assertFalse(astId.isAnd());
        Assert.assertFalse(astId.isNot());
        // Node created using a string
        ASTNode astChild = ASTNode.createIdNode("TEST");
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("ID"));
        // Gets the name of the node identifier
        Assert.assertTrue(astChild.getId() == "TEST");


    }
}
