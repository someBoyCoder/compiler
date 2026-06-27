package vm;

import codegen.Instruction;
import semantic.Type;

import java.util.*;

/**
 * Выполняет инструкции
 */
public class VirtualMachine {

    private final Object[] registers = new Object[256];
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Type> variableTypes = new HashMap<>();
    private final Deque<Integer> callStack = new ArrayDeque<>();
    private final Scanner scanner = new Scanner(System.in);

    public void execute(List<Instruction> instructions) {
        int ip = 0;

        while (ip < instructions.size()) {
            Instruction instruction = instructions.get(ip);
            Object[] args = instruction.args();

            switch (instruction.opCode()) {
                case DECLARE_VAR -> {
                    String name = (String) args[0];
                    Type type = (Type) args[1];

                    variableTypes.putIfAbsent(name, type);
                    variables.putIfAbsent(name, defaultValue(type));

                    ip++;
                }

                case LOAD_CONST -> {
                    int register = (int) args[0];
                    Object value = args[1];

                    registers[register] = value;
                    ip++;
                }

                case LOAD_VAR -> {
                    int register = (int) args[0];
                    String name = (String) args[1];

                    if (!variables.containsKey(name)) {
                        throw new RuntimeException("Переменная не найдена в VM: " + name);
                    }

                    registers[register] = variables.get(name);
                    ip++;
                }

                case STORE_VAR -> {
                    String name = (String) args[0];
                    int register = (int) args[1];

                    variables.put(name, registers[register]);
                    ip++;
                }

                case ADD -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = add(registers[left], registers[right]);
                    ip++;
                }

                case SUB -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = sub(registers[left], registers[right]);
                    ip++;
                }

                case MUL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = mul(registers[left], registers[right]);
                    ip++;
                }

                case DIV -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = div(registers[left], registers[right]);
                    ip++;
                }

                case LESS -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = toDouble(registers[left]) < toDouble(registers[right]);
                    ip++;
                }

                case LESS_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = toDouble(registers[left]) <= toDouble(registers[right]);
                    ip++;
                }

                case GREATER -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = toDouble(registers[left]) > toDouble(registers[right]);
                    ip++;
                }

                case GREATER_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = toDouble(registers[left]) >= toDouble(registers[right]);
                    ip++;
                }

                case EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    Object leftValue = registers[left];
                    Object rightValue = registers[right];

                    if (leftValue instanceof Number && rightValue instanceof Number) {
                        registers[result] = toDouble(leftValue) == toDouble(rightValue);
                    } else {
                        registers[result] = leftValue.equals(rightValue);
                    }

                    ip++;
                }

                case NOT_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    Object leftValue = registers[left];
                    Object rightValue = registers[right];

                    if (leftValue instanceof Number && rightValue instanceof Number) {
                        registers[result] = toDouble(leftValue) != toDouble(rightValue);
                    } else {
                        registers[result] = !leftValue.equals(rightValue);
                    }

                    ip++;
                }

                case JUMP_IF_TRUE -> {
                    int conditionRegister = (int) args[0];
                    int targetIndex = (int) args[1];

                    if ((Boolean) registers[conditionRegister]) {
                        ip = targetIndex;
                    } else {
                        ip++;
                    }
                }

                case PRINT -> {
                    int register = (int) args[0];

                    System.out.println(registers[register]);
                    ip++;
                }

                case INPUT -> {
                    String name = (String) args[0];

                    if (!variables.containsKey(name)) {
                        throw new RuntimeException("Переменная не найдена в VM: " + name);
                    }

                    Type type = variableTypes.get(name);
                    String value = scanner.nextLine();

                    Object parsedValue = switch (type) {
                        case INT -> Integer.parseInt(value);
                        case DOUBLE -> Double.parseDouble(value);
                        case BOOLEAN -> Boolean.parseBoolean(value);
                        case STRING -> value;
                        case ERROR -> throw new RuntimeException("Нельзя выполнить input для типа ERROR");
                    };

                    variables.put(name, parsedValue);

                    ip++;
                }

                case JUMP -> ip = (int) args[0];

                case JUMP_IF_FALSE -> {
                    int conditionRegister = (int) args[0];
                    int targetIndex = (int) args[1];

                    if (!(Boolean) registers[conditionRegister]) {
                        ip = targetIndex;
                    } else {
                        ip++;
                    }
                }

                case GOSUB -> {
                    int targetIndex = (int) args[0];

                    callStack.push(ip + 1);
                    ip = targetIndex;
                }

                case RETURN -> {
                    if (callStack.isEmpty()) {
                        throw new RuntimeException("return вызван без gosub");
                    }

                    ip = callStack.pop();
                }

                case STOP -> {
                    return;
                }
            }
        }
    }

    private boolean isDoubleOperation(Object left, Object right) {
        return left instanceof Double || right instanceof Double;
    }

    private Object add(Object left, Object right) {
        if (isDoubleOperation(left, right)) {
            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
        }

        return ((Number) left).intValue() + ((Number) right).intValue();
    }

    private Object sub(Object left, Object right) {
        if (isDoubleOperation(left, right)) {
            return ((Number) left).doubleValue() - ((Number) right).doubleValue();
        }

        return ((Number) left).intValue() - ((Number) right).intValue();
    }

    private Object mul(Object left, Object right) {
        if (isDoubleOperation(left, right)) {
            return ((Number) left).doubleValue() * ((Number) right).doubleValue();
        }

        return ((Number) left).intValue() * ((Number) right).intValue();
    }

    private Object div(Object left, Object right) {
        if (isDoubleOperation(left, right)) {
            return ((Number) left).doubleValue() / ((Number) right).doubleValue();
        }

        return ((Number) left).intValue() / ((Number) right).intValue();
    }

    private double toDouble(Object value) {
        return ((Number) value).doubleValue();
    }

    private Object defaultValue(Type type) {
        return switch (type) {
            case INT -> 0;
            case DOUBLE -> 0.0;
            case BOOLEAN -> false;
            case STRING -> "";
            case ERROR -> throw new RuntimeException("Нельзя создать переменную типа ERROR");
        };
    }
}
