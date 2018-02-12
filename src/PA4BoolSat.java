import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import bool_exp.ASTNode;
import bool_exp.BoolSatParser;

public class PA4BoolSat {

    public static void main(String[] args) {
    

        Scanner in = openInfile(args[0]);
        ASTNode root = BoolSatParser.parse(in.nextLine());
        Set<ASTNode> leafNodes = getAllLeafNodes(root);
        System.out.println(getAllLeafNodes(root).size());
        // System.out.println(root.child1.getNodeType());


    }
    public static Scanner openInfile(String filename) {
        File file = new File(filename);
        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found");
            System.exit(1);
        }
        return in;
    }


    public static void traverse() {

    }

    public static Set<ASTNode> getAllLeafNodes(ASTNode node) {
        Set<ASTNode> leafNodes = new HashSet<ASTNode>();
        if (node.getNodeType() == "ID") {
            leafNodes.add(node);
        }else if(node.getNodeType() == "NOT"){
            leafNodes.addAll(getAllLeafNodes(node.child1));
            // Otherwise recurse down the left and right children
        } else {
                leafNodes.addAll(getAllLeafNodes(node.child1));
                leafNodes.addAll(getAllLeafNodes(node.child2));
        }
        return leafNodes;
    }
}
