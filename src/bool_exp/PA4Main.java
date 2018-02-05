package bool_exp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** PA4Main -
 * @author Theodore Sackos (theodorejsackos@email.arizona.edu)
 *
 * This class will read an input file name from the command line, and print out
 * the dot representation of the Abstract Syntax Tree for the boolean logic
 * expression in the input file.
 *
 * The input file should contain only a single line, that line should contain a
 * Java boolean expression limited to:
 *      - the AND operator "&&"
 *      - the OR operator  "||"
 *      - Parenthesis "(", ")"
 *      - the NOT operator "!"
 *      - and valid Java identifiers: i.e strings with no spaces that start with
 *        a letter, '$', or '_', followed by letters, digits, '$' and '_' characters
 *
 *  -------------  Example  -------------
 * Input File:
 * ----------------------------------
 * | a && b || !c && !(!d || e)     |
 * |             ...                | <all other lines ignored>
 * ----------------------------------
 *
 * Output Dot Representation:
 * digraph expression_tree {
 *  	980546781 [label="OR"];
 *  	2061475679 [label="OR"];
 *  	140435067 [label="AND"];
 *  	1450495309 [label="ID: a" shape=box];
 *  	140435067 -> 1450495309;
 *  	1670782018 [label="ID: b" shape=box];
 *  	140435067 -> 1670782018;
 *  	2061475679 -> 140435067;
 *  	1706377736 [label="AND"];
 *  	468121027 [label="NOT"];
 *  	1804094807 [label="ID: c" shape=box];
 *  	468121027 -> 1804094807;
 *  	1706377736 -> 468121027;
 *  	951007336 [label="ID: d" shape=box];
 *  	1706377736 -> 951007336;
 *  	2061475679 -> 1706377736;
 *  	980546781 -> 2061475679;
 *  	2001049719 [label="AND"];
 *  	1528902577 [label="ID: e" shape=box];
 *  	2001049719 -> 1528902577;
 *  	1927950199 [label="NOT"];
 *  	868693306 [label="ID: f" shape=box];
 *  	1927950199 -> 868693306;
 *  	2001049719 -> 1927950199;
 *  	980546781 -> 2001049719;
 * }
 *
 * These dot graphs can be rendered into .png/.pdf/.jpg format using the command
 * line dot utility, for example:
 *      dot -Tpng dot.txt > dot.png
 * However, I have been using http://sandbox.kidstrythisathome.com/erdos/ which
 * works out really well.
 */
public class PA4Main {
    public static void main(String[] args){
        // Check that some input was provided
        if(args.length < 1 || args.length > 1){
            System.err.println("USAGE: java PA4Main <inputFile.txt>");
            System.exit(1);
        }

        // Get the expression from the file
        String expression = null;
        try(Scanner s = new Scanner(new File(args[0]));){
            expression = s.nextLine();
        }catch(FileNotFoundException e){
            System.err.printf("'%s' is not a valid file path.", args[0]);
            System.exit(1);
        }

        System.out.println(expression);

        // Call the parser to generate the AST for the expression
        ASTNode root     = BoolSatParser.parse(expression);

        // Traverse the AST and generate a dot representation
        String dotOutput = BoolSatParser.dotify(root);

        // Output the dot represenation to stdout
        System.out.println(dotOutput);
    }
}
