package parser;

import ast.*;
import lexer.Token;
import lexer.TokenType;
import semantic.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Превращает токены в дерево AST
 */
public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(parseStatement());
        }

        return new Program(statements);
    }

    private Statement parseStatement() {
        if (match(TokenType.INT)) {
            return parseDeclaration(Type.INT);
        }

        if (match(TokenType.BOOLEAN)) {
            return parseDeclaration(Type.BOOLEAN);
        }

        if (match(TokenType.PRINT)) {
            return parsePrint();
        }

        if (match(TokenType.DO)) {
            return parseDoWhile();
        }

        if (match(TokenType.FOR)) {
            return parseFor();
        }

        if (match(TokenType.SWITCH)) {
            return parseSwitch();
        }

        if (match(TokenType.BREAK)) {
            return parseBreak();
        }

        if (check(TokenType.IDENTIFIER)) {
            return parseAssignment();
        }

        throw new ParseException(peek(), "Ожидался оператор");
    }

    private Statement parseDeclaration(Type type) {
        Token name = consume(TokenType.IDENTIFIER, "Ожидалось имя переменной");
        consume(TokenType.SEMICOLON, "Ожидалась ';' после объявления переменной");

        return new Declaration(type, name.text());
    }

    private Statement parseAssignment() {
        Token name = consume(TokenType.IDENTIFIER, "Ожидалось имя переменной");
        consume(TokenType.ASSIGN, "Ожидался знак '='");

        Expression expression = parseExpression();

        consume(TokenType.SEMICOLON, "Ожидалась ';' после присваивания");

        return new Assignment(name.text(), expression);
    }

    private Statement parsePrint() {
        Expression expression = parseExpression();

        consume(TokenType.SEMICOLON, "Ожидалась ';' после print");

        return new Print(expression);
    }

    private Expression parseExpression() {
        return parseEquality();
    }

    private Expression parseEquality() {
        Expression expression = parseComparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Expression right = parseComparison();

            expression = new BinaryExpression(expression, operator.text(), right);
        }

        return expression;
    }

    private Expression parseComparison() {
        Expression expression = parseTerm();

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Expression right = parseTerm();

            expression = new BinaryExpression(expression, operator.text(), right);
        }

        return expression;
    }

    private Expression parseTerm() {
        Expression expression = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseFactor();

            expression = new BinaryExpression(expression, operator.text(), right);
        }

        return expression;
    }

    private Expression parseFactor() {
        Expression expression = parsePrimary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = parsePrimary();

            expression = new BinaryExpression(expression, operator.text(), right);
        }

        return expression;
    }

    private Expression parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new NumberExpression(Integer.parseInt(previous().text()));
        }

        if (match(TokenType.TRUE)) {
            return new BooleanExpression(true);
        }

        if (match(TokenType.FALSE)) {
            return new BooleanExpression(false);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new VariableExpression(previous().text());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Ожидалась ')' после выражения");
            return expression;
        }

        throw new ParseException(peek(), "Ожидалось выражение");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw new ParseException(peek(), message);
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Statement parseDoWhile() {
        consume(TokenType.LEFT_BRACE, "Ожидалась '{' после do");

        List<Statement> body = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            body.add(parseStatement());
        }

        consume(TokenType.RIGHT_BRACE, "Ожидалась '}' после тела цикла");
        consume(TokenType.WHILE, "Ожидалось while после тела цикла");
        consume(TokenType.LEFT_PAREN, "Ожидалась '(' после while");

        Expression condition = parseExpression();

        consume(TokenType.RIGHT_PAREN, "Ожидалась ')' после условия");
        consume(TokenType.SEMICOLON, "Ожидалась ';' после do-while");

        return new DoWhile(body, condition);
    }

    private Statement parseFor() {
        consume(TokenType.LEFT_PAREN, "Ожидалась '(' после for");

        Assignment init = parseAssignmentWithoutSemicolon();

        consume(TokenType.SEMICOLON, "Ожидалась ';' после инициализации for");

        Expression condition = parseExpression();

        consume(TokenType.SEMICOLON, "Ожидалась ';' после условия for");

        Assignment update = parseAssignmentWithoutSemicolon();

        consume(TokenType.RIGHT_PAREN, "Ожидалась ')' после for");

        consume(TokenType.LEFT_BRACE, "Ожидалась '{' после for");

        List<Statement> body = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            body.add(parseStatement());
        }

        consume(TokenType.RIGHT_BRACE, "Ожидалась '}' после тела for");

        return new For(init, condition, update, body);
    }

    private Statement parseBreak() {
        consume(TokenType.SEMICOLON, "Ожидалась ';' после break");
        return new Break();
    }

    private Statement parseSwitch() {
        consume(TokenType.LEFT_PAREN, "Ожидалась '(' после switch");

        Expression expression = parseExpression();

        consume(TokenType.RIGHT_PAREN, "Ожидалась ')' после выражения switch");
        consume(TokenType.LEFT_BRACE, "Ожидалась '{' после switch");

        List<SwitchCase> cases = new ArrayList<>();
        List<Statement> defaultBody = null;

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.CASE)) {
                cases.add(parseSwitchCase());
            } else if (match(TokenType.DEFAULT)) {
                if (defaultBody != null) {
                    throw new ParseException(previous(), "Блок default уже объявлен");
                }

                defaultBody = parseDefaultBody();
            } else {
                throw new ParseException(peek(), "Ожидался case или default");
            }
        }

        consume(TokenType.RIGHT_BRACE, "Ожидалась '}' после switch");

        return new Switch(expression, cases, defaultBody);
    }

    private List<Statement> parseDefaultBody() {
        consume(TokenType.COLON, "Ожидалась ':' после default");

        List<Statement> body = new ArrayList<>();

        while (!check(TokenType.CASE)
                && !check(TokenType.DEFAULT)
                && !check(TokenType.RIGHT_BRACE)
                && !isAtEnd()) {
            body.add(parseStatement());
        }

        return body;
    }

    private SwitchCase parseSwitchCase() {
        Expression value = parseExpression();

        consume(TokenType.COLON, "Ожидалась ':' после case");

        List<Statement> body = new ArrayList<>();

        while (!check(TokenType.CASE)
                && !check(TokenType.DEFAULT)
                && !check(TokenType.RIGHT_BRACE)
                && !isAtEnd()) {
            body.add(parseStatement());
        }

        return new SwitchCase(value, body);
    }

    private Assignment parseAssignmentWithoutSemicolon() {
        Token name = consume(TokenType.IDENTIFIER, "Ожидалось имя переменной");
        consume(TokenType.ASSIGN, "Ожидался знак '='");

        Expression expression = parseExpression();

        return new Assignment(name.text(), expression);
    }
}
