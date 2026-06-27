package ast;

import error.SourcePosition;

public record DoubleExpression(
        double value,
        SourcePosition position
) implements Expression {
}
