package codegen;

import ast.*;

import java.util.*;

/**
 * Превращает AST в инструкции
 */
public class CodeGenerator {

    private final Map<String, Integer> labelAddresses = new HashMap<>();
    private final List<GosubPatch> gosubPatches = new ArrayList<>();

    private final List<Instruction> instructions = new ArrayList<>();
    private final Deque<List<Integer>> breakJumpStack = new ArrayDeque<>();
    private int nextRegister = 0;

    private record GosubPatch(
            int instructionIndex,
            String labelName
    ) {
    }

    public List<Instruction> generate(Program program) {
        for (Statement statement : program.statements()) {
            generateStatement(statement);
        }

        patchGosubInstructions();

        return instructions;
    }

    private void generateStatement(Statement statement) {
        if (statement instanceof Declaration declaration) {
            instructions.add(new Instruction(
                    OpCode.DECLARE_VAR,
                    declaration.name(),
                    declaration.type()
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

        if (statement instanceof Input input) {
            generateInput(input);
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

        if (statement instanceof Switch switchStatement) {
            generateSwitch(switchStatement);
            return;
        }

        if (statement instanceof Break) {
            generateBreak();
            return;
        }

        if (statement instanceof Gosub gosub) {
            generateGosub(gosub);
            return;
        }

        if (statement instanceof Return) {
            generateReturn();
            return;
        }

        if (statement instanceof Label label) {
            generateLabel(label);
            return;
        }

        if (statement instanceof End) {
            generateEnd();
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

        if (expression instanceof DoubleExpression number) {
            int register = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.LOAD_CONST,
                    register,
                    number.value()
            ));

            return register;
        }

        if (expression instanceof StringExpression string) {
            int register = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.LOAD_CONST,
                    register,
                    string.value()
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

    private void generateSwitch(Switch switchStatement) {
        int switchRegister = generateExpression(switchStatement.expression());

        List<Integer> caseJumpIndexes = new ArrayList<>();

        for (SwitchCase switchCase : switchStatement.cases()) {
            int caseValueRegister = generateExpression(switchCase.value());
            int conditionRegister = allocateRegister();

            instructions.add(new Instruction(
                    OpCode.EQUAL,
                    conditionRegister,
                    switchRegister,
                    caseValueRegister
            ));

            int jumpIndex = instructions.size();

            instructions.add(new Instruction(
                    OpCode.JUMP_IF_TRUE,
                    conditionRegister,
                    -1
            ));

            caseJumpIndexes.add(jumpIndex);
        }

        int defaultJumpIndex = instructions.size();

        instructions.add(new Instruction(
                OpCode.JUMP,
                -1
        ));

        List<Integer> breakJumps = new ArrayList<>();
        breakJumpStack.push(breakJumps);

        for (int i = 0; i < switchStatement.cases().size(); i++) {
            SwitchCase switchCase = switchStatement.cases().get(i);

            int caseStartIndex = instructions.size();

            instructions.set(
                    caseJumpIndexes.get(i),
                    new Instruction(
                            OpCode.JUMP_IF_TRUE,
                            instructions.get(caseJumpIndexes.get(i)).args()[0],
                            caseStartIndex
                    )
            );

            for (Statement statement : switchCase.body()) {
                generateStatement(statement);
            }

            int autoJumpIndex = instructions.size();

            instructions.add(new Instruction(
                    OpCode.JUMP,
                    -1
            ));

            breakJumps.add(autoJumpIndex);
        }

        int defaultStartIndex = instructions.size();

        instructions.set(
                defaultJumpIndex,
                new Instruction(
                        OpCode.JUMP,
                        defaultStartIndex
                )
        );

        if (switchStatement.defaultBody() != null) {
            for (Statement statement : switchStatement.defaultBody()) {
                generateStatement(statement);
            }
        }

        int switchEndIndex = instructions.size();

        for (Integer breakJumpIndex : breakJumpStack.pop()) {
            instructions.set(
                    breakJumpIndex,
                    new Instruction(
                            OpCode.JUMP,
                            switchEndIndex
                    )
            );
        }
    }

    private void generateBreak() {
        if (breakJumpStack.isEmpty()) {
            throw new RuntimeException("break вне switch");
        }

        int jumpIndex = instructions.size();

        instructions.add(new Instruction(
                OpCode.JUMP,
                -1
        ));

        breakJumpStack.peek().add(jumpIndex);
    }

    private void generateInput(Input input) {
        instructions.add(new Instruction(
                OpCode.INPUT,
                input.variableName()
        ));
    }

    private void generateGosub(Gosub gosub) {
        int instructionIndex = instructions.size();

        instructions.add(new Instruction(
                OpCode.GOSUB,
                -1
        ));

        gosubPatches.add(new GosubPatch(instructionIndex, gosub.labelName()));
    }

    private void generateReturn() {
        instructions.add(new Instruction(
                OpCode.RETURN
        ));
    }

    private void generateLabel(Label label) {
        labelAddresses.put(label.name(), instructions.size());
    }

    private void generateEnd() {
        instructions.add(new Instruction(
                OpCode.STOP
        ));
    }

    private void patchGosubInstructions() {
        for (GosubPatch patch : gosubPatches) {
            Integer labelAddress = labelAddresses.get(patch.labelName());

            if (labelAddress == null) {
                throw new RuntimeException("Метка не найдена при генерации кода: " + patch.labelName());
            }

            instructions.set(
                    patch.instructionIndex(),
                    new Instruction(
                            OpCode.GOSUB,
                            labelAddress
                    )
            );
        }
    }

    private int allocateRegister() {
        return nextRegister++;
    }
}
