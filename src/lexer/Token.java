package lexer;

/**
 * Сущность токена
 * @param type тип токена
 * @param text реальный текст
 * @param line строка
 * @param column колонка
 */
public record Token(
        TokenType type,
        String text,
        int line,
        int column
) {
}