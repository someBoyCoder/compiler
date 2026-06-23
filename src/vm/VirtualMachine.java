package vm;

import codegen.Instruction;
import semantic.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Выполняет инструкции
 */
public class VirtualMachine {

    private final Object[] registers = new Object[256];
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Type> variableTypes = new HashMap<>();
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

                    registers[result] = (Integer) registers[left] + (Integer) registers[right];
                    ip++;
                }

                case SUB -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] - (Integer) registers[right];
                    ip++;
                }

                case MUL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] * (Integer) registers[right];
                    ip++;
                }

                case DIV -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] / (Integer) registers[right];
                    ip++;
                }

                case LESS -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] < (Integer) registers[right];
                    ip++;
                }

                case LESS_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] <= (Integer) registers[right];
                    ip++;
                }

                case GREATER -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] > (Integer) registers[right];
                    ip++;
                }

                case GREATER_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = (Integer) registers[left] >= (Integer) registers[right];
                    ip++;
                }

                case EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = registers[left].equals(registers[right]);
                    ip++;
                }

                case NOT_EQUAL -> {
                    int result = (int) args[0];
                    int left = (int) args[1];
                    int right = (int) args[2];

                    registers[result] = !registers[left].equals(registers[right]);
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
                        case BOOLEAN -> Boolean.parseBoolean(value);
                        case STRING -> value;
                    };

                    variables.put(name, parsedValue);

                    ip++;
                }

                case JUMP -> {
                    int targetIndex = (int) args[0];

                    ip = targetIndex;
                }

                case JUMP_IF_FALSE -> {
                    int conditionRegister = (int) args[0];
                    int targetIndex = (int) args[1];

                    if (!(Boolean) registers[conditionRegister]) {
                        ip = targetIndex;
                    } else {
                        ip++;
                    }
                }
            }
        }
    }

    private Object defaultValue(Type type) {
        return switch (type) {
            case INT -> 0;
            case BOOLEAN -> false;
            case STRING -> "";
        };
    }
}
