package parser;

import lexer.Token;

public class ParseException extends RuntimeException {
    public ParseException(Token token, String message) {
        super("Ошибка [" + token.line() + ":" + token.column() + "]: "
                + message + ". Найдено: '" + token.text() + "'");
    }
}
