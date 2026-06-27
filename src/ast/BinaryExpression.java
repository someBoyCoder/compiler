package ast;

import error.SourcePosition;

public record BinaryExpression(
        Expression left,
        String operator,
        Expression right,
        SourcePosition position
) implements Expression {
}
