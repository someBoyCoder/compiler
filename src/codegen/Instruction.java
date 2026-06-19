package codegen;

public record Instruction(
        OpCode opCode,
        Object... args
) {
}
