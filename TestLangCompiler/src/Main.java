import java_cup.runtime.*;
import ast.*;
import codegen.CodeGenerator;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Main <input.test> [output.java]");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : "output/GeneratedTests.java";

        try {
            // Create lexer and parser
            FileReader reader = new FileReader(inputFile);
            Lexer lexer = new Lexer(reader);
            parser parser = new parser(lexer);

            // Parse
            System.out.println("Parsing " + inputFile + "...");
            Symbol parseResult = parser.parse();
            Program program = (Program) parseResult.value;

            System.out.println("Parse successful!");
            System.out.println("Found " + program.tests.size() + " test(s)");

            // Validate
            validateProgram(program);

            // Generate code
            System.out.println("Generating code...");
            CodeGenerator generator = new CodeGenerator(program);
            String javaCode = generator.generate();

            // Write output
            File outFile = new File(outputFile);
            outFile.getParentFile().mkdirs();

            try (PrintWriter writer = new PrintWriter(outFile)) {
                writer.print(javaCode);
            }

            System.out.println("Generated: " + outputFile);
            System.out.println("Success!");

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found: " + inputFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void validateProgram(Program program) throws Exception {
        if (program.tests.isEmpty()) {
            throw new Exception("No test blocks found");
        }

        for (TestBlock test : program.tests) {
            int requestCount = 0;
            int assertionCount = 0;

            for (Statement stmt : test.statements) {
                if (stmt instanceof HttpRequest) {
                    requestCount++;
                } else if (stmt instanceof Assertion) {
                    assertionCount++;
                }
            }

            if (requestCount == 0) {
                throw new Exception("Test '" + test.name + "' must have at least one HTTP request");
            }
            if (assertionCount < 2) {
                throw new Exception("Test '" + test.name + "' must have at least 2 assertions");
            }
        }
    }
}