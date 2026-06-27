import ast.Program;
import codegen.CodeGenerator;
import codegen.Instruction;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import semantic.SemanticAnalyzer;
import vm.VirtualMachine;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String source = """
        print "Начало";

        gosub hello;

        print "Конец";

        end;

        hello:
            print "Привет из gosub";
            return;
        """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(program);

        CodeGenerator codeGenerator = new CodeGenerator();
        List<Instruction> instructions = codeGenerator.generate(program);

        System.out.println("Сгенерированные инструкции:");
        for (Instruction instruction : instructions) {
            System.out.println(instruction.opCode() + " " + List.of(instruction.args()));
        }

        System.out.println("Результат выполнения:");
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.execute(instructions);
    }
}