import org.junit.Assert;
import org.junit.Test;

import bool_exp.ASTNode;

// Junit testing of the ASTNode class
public class ASTNodeTest {
    // Tests the functions of the AND node type
	@Test
	public void testAND() {
        // Creates a node using null as children and confirms creation
        ASTNode astAnd = ASTNode.createAndNode(null, null);
        Assert.assertTrue(astAnd.child1 == null);
        Assert.assertTrue(astAnd.child2 == null);
        // Asserts to confirm type
        Assert.assertTrue(astAnd.isAnd());
        Assert.assertFalse(astAnd.isId());
        Assert.assertFalse(astAnd.isOr());
        Assert.assertFalse(astAnd.isNot());
        // Node created using a node
        ASTNode astChild = ASTNode.createAndNode(astAnd, astAnd);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("AND"));
        // Ensures a child was produced correctly
        Assert.assertTrue(astChild.child1.equals(astAnd));
        Assert.assertTrue(astChild.child2.equals(astAnd));



	}

    // Tests the functions of the OR node type
    @Test
    public void testOR() {
        // Creates a node using null as children and confirms cration
        ASTNode astOr = ASTNode.createOrNode(null, null);
        Assert.assertTrue(astOr.child1 == null);
        Assert.assertTrue(astOr.child2 == null);
        // Asserts to confirm type
        Assert.assertTrue(astOr.isOr());
        Assert.assertFalse(astOr.isAnd());
        Assert.assertFalse(astOr.isId());
        Assert.assertFalse(astOr.isNot());
        // Node created using a node
        ASTNode astChild = ASTNode.createOrNode(astOr, astOr);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("OR"));
        // Ensures a child was produced correctly
        Assert.assertTrue(astChild.child1.equals(astOr));
        Assert.assertTrue(astChild.child2.equals(astOr));
    }

    // Tests the functions of the NOT node type
    @Test
    public void testNot() {
        // Creates a node using null as children and confirms creation
        ASTNode astNot = ASTNode.createNotNode(null);
        Assert.assertTrue(astNot.child1 == null);
        // Asserts to confirm type
        Assert.assertTrue(astNot.isNot());
        Assert.assertFalse(astNot.isOr());
        Assert.assertFalse(astNot.isAnd());
        Assert.assertFalse(astNot.isId());
        // Node created using a node
        ASTNode astChild = ASTNode.createNotNode(astNot);
        // Gets the name of the node type
        Assert.assertTrue(astChild.getNodeType().equals("NOT"));
        // Ensures a child was produced correctly
        Assert.assertTrue(astChild.child1.equals(astNot));

    }

    // Tests the functions of the Id node type
    @Test
    public void testId() {
        // Creates a node using null as children and confirms creation
        ASTNode astId = ASTNode.createIdNode(null);
        Assert.assertTrue(astId.child1 == null);
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
