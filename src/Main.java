
/**
 * Use the Python test runner for automated testing:
 * 
 * python test_runner.py              # Test all cases
 * python test_runner.py --type type1 # Test only type1  
 * python test_runner.py --type type2 # Test only type2
 * python test_runner.py --verbose    # Show detailed diffs
 * python test_runner.py --benchmark  # Performance testing (no comparison)
 * 
 * Flags can be combined, e.g.:
 * python test_runner.py -bv              # benchmark + verbose
 * python test_runner.py -bv --type type1 # benchmark + verbose + type1
 * python test_runner.py -b --type type2  # benchmark + type2
 * 
 * MANUAL TESTING (For Individual Runs):
 * ======================================
 * 
 * 1. Compile: cd src/ && javac *.java
 * 2. Run: java Main ../testcase_inputs/test.txt ../output/test.txt
 * 3. Compare output with expected results
 * 
 * PROJECT STRUCTURE:
 * ==================
 * 
 * project_root/
 * ├── src/
 * ├── testcase_inputs/
 * ├── testcase_outputs/
 * ├── output/
 * └── test_runner.py
 * 
 * REQUIREMENTS:
 * =============
 * - Java SDK 8+ (javac, java commands)
 * - Python 3.6+ (for test runner)
 * 
 * @author Alperen Ulu
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.math.*;

public class Main {
    public static void main(String[] args) {
        // Check command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            System.out.println("Example: java Main ../testcase_inputs/test.txt ../output/test.txt");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        // Initialize file reader
        Scanner reader = null;
        try {
            reader = new Scanner(new File(inFile));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + inFile);
            e.printStackTrace();
            return;
        }

        // Initialize file writer
        FileWriter writer = null;
        try {
            writer = new FileWriter(outFile);
        } catch (IOException e) {
            System.out.println("Writing error: " + outFile);
            e.printStackTrace();
            if (reader != null)
                reader.close();
            return;
        }
        GameManager lineProcessor = new GameManager();
        // Process commands line by line
        try {
            while (reader.hasNext()) {
                String line = reader.nextLine();
                Scanner scanner = new Scanner(line);
                String command = scanner.next();
                String out = "";

                switch (command) {
                    case "draw_card": {
                        String name = "";
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            name = scanner.next();
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = lineProcessor.drawCard(name, att, hp); // suggested method for draw_card command
                        break;
                    }
                    case "battle": {
                        int att = 0;
                        int hp = 0;
                        int heal = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        if (scanner.hasNext())
                            heal = scanner.nextInt();
                        out = lineProcessor.startBattle(att, hp, heal); // suggested method for battle command
                        break;
                    }
                    case "find_winning": {
                        out = lineProcessor.findWinner(); // suggested method for find_winning command
                        break;
                    }
                    case "deck_count": {
                        out = lineProcessor.countDeck(); // suggested method for deck_count command
                        break;
                    }


                     case "discard_pile_count": {
                     out = lineProcessor.countDiscardDeck(); // suggested method for discard_pile_count command
                     break;
                     }

                    case "steal_card": {
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = lineProcessor.stealCard(att , hp); // suggested method for steal_card command
                        break;
                    }
                    default: {
                        System.out.println("Invalid command: " + command);
                        scanner.close();
                        writer.close();
                        reader.close();
                        return;
                    }
                }

                scanner.close();

                try {
                    writer.write(out);
                    writer.write("\n"); // uncomment if each output needs to be in a new line and
                    // you did not implement that inside the functions.
                } catch (IOException e2) {
                    System.out.println("Writing error");
                    e2.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Error processing commands: " + e.getMessage());
            e.printStackTrace();
        }

        // Clean up resources
        try {
            writer.close();
        } catch (IOException e2) {
            System.out.println("Writing error");
            e2.printStackTrace();
        }

        if (reader != null) {
            reader.close();
        }
        System.out.println("end");
    }
}
