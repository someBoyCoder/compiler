package ast;

import error.SourcePosition;

public record NumberExpression(
        int value,
        SourcePosition position
) implements Expression {
}
