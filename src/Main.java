import ast.Program;
import codegen.CodeGenerator;
import codegen.Instruction;
import error.ErrorReporter;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import semantic.SemanticAnalyzer;
import vm.VirtualMachine;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String source = """
                int x;
                int x;

                y = 5;

                boolean flag;
                flag = 10;

                int z;
                z = "hello";

                do {
                    print z;
                } while (z);
                """;

        ErrorReporter errorReporter = new ErrorReporter();

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens, errorReporter);
        Program program = parser.parse();

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(errorReporter);
        semanticAnalyzer.analyze(program);

        if (errorReporter.hasErrors()) {
            errorReporter.printErrors();
            return;
        }

        CodeGenerator codeGenerator = new CodeGenerator();
        List<Instruction> instructions = codeGenerator.generate(program);

        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.execute(instructions);
    }
}