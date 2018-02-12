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

/*
 * INSTRUCTIONS
 * 
 * this program determines the satisfiability of a given expression
 * and prints under what conditions it can be satisfied, if at all.
 * 
 * The expression input file should contain only a single line, that line should
 * contain a well-formed Java boolean expression limited to:
 * the AND operator &&
 * the OR operator ||
 * Parenthesis (, )
 * the NOT operator !
 * and valid Java identifiers: i.e strings with no spaces that start with
 * exactly one letter, or $, or _, that is followed by any number of letters,
 * digits, $ and _ characters.
 * 
 * This expression must be on the first line as
 * ALL OTHER LINES WILL BE IGNORED
 * 
 * A debug mode is availabe for this program, input DEBUG on the second command
 * line and every enumeration of the given variables will be printed along with
 * whether they evaluated to true or false
 * 
 * In their respective cases, the ouput will look like
 * input: "given expression"
 * SAT or UNSAT, depending on whether the expression was satisfiable
 * enumeration1
 * enumeration3
 * enumeration5
 * ...
 * Or if debug mode is enabled
 * enumeration1, "truth value"
 * enumeration2, "truth value"
 * enumeration3, "truth value"
 * enumeration4, "truth value"
 * enumeration5, "truth value"
 * ...
 * 
 * Written by: Nicholas Hernandez
 * 
 */

public class PA4BoolSat {

    // Method calls for each part of the program
    public static void main(String[] args) {
        // Creates the scanner from an input file
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
        // Edits endMap with enumerations of each variable being true or false
        enumerate(sofar, leafNodes.size(), leafNodes, 0, endMap, root);
        // Prints the enumerations and whether the expression is satisfiable
        printAll(endMap, args, command);
    }

    // Returns a scanner for a given file
    public static Scanner openInfile(String filename) {
        // Initializes file and scanner
        File file = new File(filename);
        Scanner in = null;
        // error handling for missing file
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found");
            System.exit(1);
        }
        return in;
    }

    // Recusrively returns the variables at leaf nodes in a hashset from a given
    // ASTNode
    public static Set<String> getAllLeafNodes(ASTNode node) {
        Set<String> leafNodes = new HashSet<String>();
        // Basecase has been reached and returns the node name
        if (node.getNodeType() == "ID") {
            leafNodes.add(node.getId());
            // If the node is of the not variety then it is unable to recurse
            // down child2
        } else if (node.getNodeType() == "NOT") {
            leafNodes.addAll(getAllLeafNodes(node.child1));
            // Otherwise recurse down the left and right children
        } else {
                leafNodes.addAll(getAllLeafNodes(node.child1));
                leafNodes.addAll(getAllLeafNodes(node.child2));
        }
        return leafNodes;
    }

    // Enumerates all combinations of true and false for a given set of
    // variables
    // while doing so, enumerate edits a hashmap "endMap" with a string
    // representation of the enumeration as its key and whether it is
    // satisfiable as its value
    public static void enumerate(int[] sofar, int size,
            Set<String> leafNodes, int k, HashMap<String, Boolean> endMap,
            ASTNode root) {
        // Base case is reached
        if (k == size) {
            // Assigns true/false to variables based on enumeration
            HashMap<String, Boolean> bool = assignBool(sofar, leafNodes);
            // Creates a string representation of the enumeration
            String assignment = assign(sofar, bool);
            // edits the ending hashmap
            endMap.put(assignment, process(bool, root));
            return;
        }
        // All possible local decisions
        for (int i = 0; i < 2; i++) {
            sofar[k] = i; // Local decision
            enumerate(sofar, size, leafNodes, k + 1, endMap, root);
            sofar[k] = 0; // Local decision undone

        }
    }

    // Processes a tree recursively and returns whether it is satisfiable
    public static boolean process(HashMap<String, Boolean> assignBool,
            ASTNode root) {

        // Returns the and of child1 and child2
        if (root.getNodeType() == "AND") {
            return process(assignBool, root.child1)
                    && process(assignBool, root.child2);
            // Returns the or of child1 and child2
        } else if (root.getNodeType() == "OR") {
            return process(assignBool, root.child1)
                    || process(assignBool, root.child2);

            // returns the opposite of what was processed
        } else if (root.getNodeType() == "NOT") {
            return !process(assignBool, root.child1);

            // the only node type left is Id whose value is returned
        } else {
            return assignBool.get(root.getId());
        }
    }

    // Returns a lexicographically sorted(based on keys) string of an
    // enumeration
    public static String assign(int[] sofar, HashMap<String, Boolean> bool) {
        String str="";
        // Sorts the hashmap of keys
        ArrayList<String> list = new ArrayList<String>(bool.keySet());
        Collections.sort(list);
        // Creates a string representation of the enumeration
        for (String s : list) {
            str = str + s + ": " + bool.get(s) + ", ";
        }
        return str;
    }

    // Returns a hashmap of variables to true or false based on an enumeration
    public static HashMap<String, Boolean> assignBool(int[] sofar,
            Set<String> leafNodes) {
        // Initializations
        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        int iter = 0;
        Boolean value = null;
        // Places each variable to its true or false value in the hashmap
        for (String s : leafNodes) {
            // Converts 0 or 1 to false or true
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

    // Lexicographically prints the possible enumerations
    public static void printAll(HashMap<String, Boolean> endMap,
            String[] args, String command) {
        // Initializations
        ArrayList<String> list = new ArrayList<String>();
        // Sat is whether a command was satisfiable
        String sat = "UNSAT";
        // If debug mode is enabled all enumerations are gathered
        if (args.length == 2) {
            for (String s : endMap.keySet()) {
                list.add(s + endMap.get(s));
                if (endMap.get(s) == true) {
                    sat = "SAT";
                }
            }
            // No debug mode, only true enumerations are gathered
        } else {
            for (String s : endMap.keySet()) {
                if (endMap.get(s) == true) {
                    list.add(s.substring(0, s.length() - 2));
                    sat = "SAT";
                }
            }
        }
        // Sorts the list of enumerations
        Collections.sort(list);
        // prints input, satisfiability, and enumerations with/without debug
        // enabled
        System.out.println("input: " + command);
        System.out.println(sat);
        for (String s : list) {
            System.out.println(s);
        }
    }
}
