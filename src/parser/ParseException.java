package parser;

import error.SourcePosition;
import lexer.Token;

public class ParseException extends RuntimeException {

    private final SourcePosition position;

    public ParseException(Token token, String message) {
        super(message + ". Найдено: '" + token.text() + "'");
        this.position = SourcePosition.from(token);
    }

    public SourcePosition position() {
        return position;
    }
}
