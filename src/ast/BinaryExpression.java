package ast;

public record BinaryExpression(
        Expression left,
        String operator,
        Expression right
) implements Expression {
}
