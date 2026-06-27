package ast;

import error.SourcePosition;

public record BooleanExpression(
        boolean value,
        SourcePosition position
) implements Expression {
}
