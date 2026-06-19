package lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Разбивает текст на токены
 */
public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int pos = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            char c = peek();

            if (Character.isWhitespace(c)) {
                skipWhitespace();
            } else if (Character.isLetter(c)) {
                readIdentifierOrKeyword();
            } else if (Character.isDigit(c)) {
                readNumber();
            } else {
                readSymbol();
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private void readIdentifierOrKeyword() {
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && Character.isLetterOrDigit(peek())) {
            sb.append(advance());
        }

        String text = sb.toString();

        TokenType type = switch (text) {
            case "int" -> TokenType.INT;
            case "boolean" -> TokenType.BOOLEAN;
            case "print" -> TokenType.PRINT;
            case "do" -> TokenType.DO;
            case "while" -> TokenType.WHILE;
            case "for" -> TokenType.FOR;
            case "true" -> TokenType.TRUE;
            case "false" -> TokenType.FALSE;
            default -> TokenType.IDENTIFIER;
        };

        tokens.add(new Token(type, text, line, startColumn));
    }

    private void readNumber() {
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && Character.isDigit(peek())) {
            sb.append(advance());
        }

        tokens.add(new Token(TokenType.NUMBER, sb.toString(), line, startColumn));
    }

    private void readSymbol() {
        int startColumn = column;
        char c = advance();

        switch (c) {
            case '=' -> {
                if (match('=')) {
                    tokens.add(new Token(TokenType.EQUAL_EQUAL, "==", line, startColumn));
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "=", line, startColumn));
                }
            }

            case '!' -> {
                if (match('=')) {
                    tokens.add(new Token(TokenType.BANG_EQUAL, "!=", line, startColumn));
                } else {
                    throw new RuntimeException("Неизвестный символ '!' в строке " + line + ", колонка " + startColumn);
                }
            }

            case '<' -> {
                if (match('=')) {
                    tokens.add(new Token(TokenType.LESS_EQUAL, "<=", line, startColumn));
                } else {
                    tokens.add(new Token(TokenType.LESS, "<", line, startColumn));
                }
            }

            case '>' -> {
                if (match('=')) {
                    tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", line, startColumn));
                } else {
                    tokens.add(new Token(TokenType.GREATER, ">", line, startColumn));
                }
            }

            case '+' -> tokens.add(new Token(TokenType.PLUS, "+", line, startColumn));
            case '-' -> tokens.add(new Token(TokenType.MINUS, "-", line, startColumn));
            case '*' -> tokens.add(new Token(TokenType.STAR, "*", line, startColumn));
            case '/' -> tokens.add(new Token(TokenType.SLASH, "/", line, startColumn));
            case ';' -> tokens.add(new Token(TokenType.SEMICOLON, ";", line, startColumn));
            case '(' -> tokens.add(new Token(TokenType.LEFT_PAREN, "(", line, startColumn));
            case ')' -> tokens.add(new Token(TokenType.RIGHT_PAREN, ")", line, startColumn));
            case '{' -> tokens.add(new Token(TokenType.LEFT_BRACE, "{", line, startColumn));
            case '}' -> tokens.add(new Token(TokenType.RIGHT_BRACE, "}", line, startColumn));

            default -> throw new RuntimeException("Неизвестный символ '" + c + "' в строке " + line + ", колонка " + startColumn);
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(pos) != expected) {
            return false;
        }

        pos++;
        column++;
        return true;
    }

    private void skipWhitespace() {
        char c = advance();

        if (c == '\n') {
            line++;
            column = 1;
        }
    }

    private char advance() {
        char c = source.charAt(pos);
        pos++;
        column++;
        return c;
    }

    private char peek() {
        return source.charAt(pos);
    }

    private boolean isAtEnd() {
        return pos >= source.length();
    }
}
