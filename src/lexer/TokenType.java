package lexer;

/**
 * Набор слов и символов которые понимает наш ЯП
 */
public enum TokenType {
    INT,
    DOUBLE,
    BOOLEAN,
    STRING_TYPE,

    PRINT,
    INPUT,

    DO,
    WHILE,
    FOR,
    SWITCH,
    CASE,
    DEFAULT,
    BREAK,
    COLON,

    GOSUB,
    RETURN,
    END,

    TRUE,
    FALSE,

    IDENTIFIER,
    NUMBER,
    STRING,

    ASSIGN,          // =
    EQUAL_EQUAL,     // ==
    BANG_EQUAL,      // !=

    LESS,            // <
    LESS_EQUAL,      // <=
    GREATER,         // >
    GREATER_EQUAL,   // >=

    PLUS,            // +
    MINUS,           // -
    STAR,            // *
    SLASH,           // /

    SEMICOLON,       // ;
    LEFT_PAREN,      // (
    RIGHT_PAREN,     // )
    LEFT_BRACE,      // {
    RIGHT_BRACE,     // }

    EOF
}
