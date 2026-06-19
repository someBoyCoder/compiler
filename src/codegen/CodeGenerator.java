package codegen;

import ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Превращает AST в инструкции
 */
public class CodeGenerator {

    private final List<Instruction> instructions = new ArrayList<>();
    private int nextRegister = 0;

    public List<Instruction> generate(Program program) {
        for (Statement statement : program.statements()) {
            generateStatement(statement);
        }

        return instructions;
    }

    private void generateStatement(Statement statement) {
        if (statement instanceof Declaration declaration) {
            instructions.add(new Instruction(
                    OpCode.DECLARE_VAR,
                    declaration.name()
            ));
            return;
        }

        if (statement instanceof Assignment assignment) {
            int expressionRegister = generateExpression(assignment.expression());

            instructions.add(new Instruction(
                    OpCode.STORE_VAR,
                    assignment.name(),
                    expressionRegister
            ));
            return;
        }

        if (statement instanceof Print print) {
            int expressionRegister = generateExpression(print.expression());

            instructions.add(new Instruction(
                    OpCode.PRINT,
                    expressionRegister
            ));
            return;
        }

        if (statement instanceof DoWhile doWhile) {
            generateDoWhile(doWhile);
            return;
        }

        if (statement instanceof For forStatement) {
            generateFor(forStatement);
            return;
        }

        throw new RuntimeException("Неизвестный оператор: " + statement.getClass().getSimpleName());
    }

    private int generateExpression(Expression expression) {
        if (expression instanceof NumberExpression number) {
            int register = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.LOAD_CONST,
                    register,
                    number.value()
            ));

            return register;
        }

        if (expression instanceof BooleanExpression bool) {
            int register = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.LOAD_CONST,
                    register,
                    bool.value()
            ));

            return register;
        }

        if (expression instanceof VariableExpression variable) {
            int register = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.LOAD_VAR,
                    register,
                    variable.name()
            ));

            return register;
        }

        if (expression instanceof BinaryExpression binary) {
            int leftRegister = generateExpression(binary.left());
            int rightRegister = generateExpression(binary.right());
            int resultRegister = allocateRegister();

            OpCode opCode = switch (binary.operator()) {
                case "+" -> OpCode.ADD;
                case "-" -> OpCode.SUB;
                case "*" -> OpCode.MUL;
                case "/" -> OpCode.DIV;
                case "<" -> OpCode.LESS;
                case "<=" -> OpCode.LESS_EQUAL;
                case ">" -> OpCode.GREATER;
                case ">=" -> OpCode.GREATER_EQUAL;
                case "==" -> OpCode.EQUAL;
                case "!=" -> OpCode.NOT_EQUAL;
                default -> throw new RuntimeException("Неизвестный оператор: " + binary.operator());
            };

            instructions.add(new Instruction(
                    opCode,
                    resultRegister,
                    leftRegister,
                    rightRegister
            ));

            return resultRegister;
        }

        throw new RuntimeException("Неизвестное выражение: " + expression.getClass().getSimpleName());
    }

    private void generateDoWhile(DoWhile doWhile) {
        int loopStartIndex = instructions.size();

        for (Statement statement : doWhile.body()) {
            generateStatement(statement);
        }

        int conditionRegister = generateExpression(doWhile.condition());

        instructions.add(new Instruction(
                OpCode.JUMP_IF_TRUE,
                conditionRegister,
                loopStartIndex
        ));
    }

    private void generateFor(For forStatement) {
        generateAssignment(forStatement.init());

        int loopStartIndex = instructions.size();

        int conditionRegister = generateExpression(forStatement.condition());

        int jumpIfFalseIndex = instructions.size();

        instructions.add(new Instruction(
                OpCode.JUMP_IF_FALSE,
                conditionRegister,
                -1
        ));

        for (Statement statement : forStatement.body()) {
            generateStatement(statement);
        }

        generateAssignment(forStatement.update());

        instructions.add(new Instruction(
                OpCode.JUMP,
                loopStartIndex
        ));

        int loopEndIndex = instructions.size();

        instructions.set(
                jumpIfFalseIndex,
                new Instruction(
                        OpCode.JUMP_IF_FALSE,
                        conditionRegister,
                        loopEndIndex
                )
        );
    }

    private void generateAssignment(Assignment assignment) {
        int expressionRegister = generateExpression(assignment.expression());

        instructions.add(new Instruction(
                OpCode.STORE_VAR,
                assignment.name(),
                expressionRegister
        ));
    }

    private int allocateRegister() {
        return nextRegister++;
    }
}
