import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import bool_exp.ASTNode;
import bool_exp.BoolSatParser;

public class PA4BoolSat {

    public static void main(String[] args) {
        // Creates the scanner
        Scanner in = openInfile(args[0]);
        // Collects the expression command
        String command = in.nextLine();
        // Creates a root node from a command
        ASTNode root = BoolSatParser.parse(command);
        // Creates a hashset of all variables
        Set<String> leafNodes = getAllLeafNodes(root);
        // Initializes information holding structures
        int[] sofar = new int[leafNodes.size()];
        HashMap<String, Boolean> endMap = new HashMap<String, Boolean>();
        // Edits endMap with
        enumerate(sofar, leafNodes.size(), leafNodes, 0, endMap, root);
        printAll(endMap, args, command);


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

    public static Set<String> getAllLeafNodes(ASTNode node) {
        Set<String> leafNodes = new HashSet<String>();
        if (node.getNodeType() == "ID") {
            leafNodes.add(node.getId());
        }else if(node.getNodeType() == "NOT"){
            leafNodes.addAll(getAllLeafNodes(node.child1));
            // Otherwise recurse down the left and right children
        } else {
                leafNodes.addAll(getAllLeafNodes(node.child1));
                leafNodes.addAll(getAllLeafNodes(node.child2));
        }
        return leafNodes;
    }

    public static void enumerate(int[] sofar, int size,
            Set<String> leafNodes, int k, HashMap<String, Boolean> endMap,
            ASTNode root) {
        if (k == size) {

            HashMap<String, Boolean> bool = assignBool(sofar, leafNodes);
            String assignment = assign(sofar, bool);
            endMap.put(assignment, process(sofar, leafNodes, bool, root));
            return;
        }
        for (int i = 0; i < 2; i++) {
            sofar[k] = i;
            enumerate(sofar, size, leafNodes, k + 1, endMap, root);
            sofar[k] = 0;

        }


    }

    public static boolean process(int[] sofar, Set<String> leafNodes,
            HashMap<String, Boolean> assignBool, ASTNode root) {
        if (root.getNodeType() == "AND") {
            return process(sofar, leafNodes, assignBool, root.child1)
                    && process(sofar, leafNodes, assignBool, root.child2);
        } else if (root.getNodeType() == "OR") {
            return process(sofar, leafNodes, assignBool, root.child1)
                    || process(sofar, leafNodes, assignBool, root.child2);
        } else if (root.getNodeType() == "NOT") {
            return !process(sofar, leafNodes, assignBool, root.child1);
        } else {
            return assignBool.get(root.getId());
        }
        


    }

    public static String assign(int[] sofar, HashMap<String, Boolean> bool) {
        String str="";
        int iter = 0;
        ArrayList<String> list = new ArrayList<String>(bool.keySet());
        Collections.sort(list);
        for (String s : list) {
            str = str + s + ": " + bool.get(s) + ", ";
            iter = iter + 1;
        }
        return str;
    }

    public static HashMap<String, Boolean> assignBool(int[] sofar,
            Set<String> leafNodes) {
        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        int iter = 0;
        Boolean value = null;
        for (String s : leafNodes) {
            if (sofar[iter] == 0) {
                value = false;
            } else {
                value = true;
            }
            hashMap.put(s, value);
            iter = iter + 1;
        }
        return hashMap;
    }

    public static void printAll(HashMap<String, Boolean> endMap,
            String[] args, String command) {
        ArrayList<String> list = new ArrayList<String>();
        String sat = "UNSAT";
        if (args.length == 2) {
            for (String s : endMap.keySet()) {
                list.add(s + endMap.get(s));
                if (endMap.get(s) == true) {
                    sat = "SAT";
                }
            }
        } else {
            for (String s : endMap.keySet()) {
                if (endMap.get(s) == true) {
                    list.add(s.substring(0, s.length() - 2));
                    sat = "SAT";
                }
            }
        }
        Collections.sort(list);
        System.out.println("input: " + command);
        System.out.println(sat);
        for (String s : list) {
            System.out.println(s);
        }
    }
}
