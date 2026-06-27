package error;

import lexer.Token;

public record SourcePosition(
        int line,
        int column
) {
    public static SourcePosition from(Token token) {
        return new SourcePosition(token.line(), token.column());
    }

    @Override
    public String toString() {
        return "[" + line + ":" + column + "]";
    }
}
