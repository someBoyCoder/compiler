package codegen;

public enum OpCode {
    DECLARE_VAR,

    LOAD_CONST,
    LOAD_VAR,
    STORE_VAR,

    ADD,
    SUB,
    MUL,
    DIV,

    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    EQUAL,
    NOT_EQUAL,

    JUMP,
    JUMP_IF_TRUE,
    JUMP_IF_FALSE,

    PRINT,
    INPUT
}
