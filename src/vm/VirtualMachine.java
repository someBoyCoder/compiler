package vm;

import codegen.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Выполняет инструкции
 */
public class VirtualMachine {

    private final Object[] registers = new Object[256];
    private final Map<String, Object> variables = new HashMap<>();

    public void execute(List<Instruction> instructions) {
        int ip = 0;

        while (ip < instructions.size()) {
            Instruction instruction = instructions.get(ip);
            Object[] args = instruction.args();

            switch (instruction.opCode()) {
                case DECLARE_VAR -> {
                    String name = (String) args[0];
                    variables.putIfAbsent(name, null);
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
}
