package ast;

public record BooleanExpression(
        boolean value
) implements Expression {
}
