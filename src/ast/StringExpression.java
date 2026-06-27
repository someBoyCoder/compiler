package ast;

import error.SourcePosition;

public record StringExpression(
        String value,
        SourcePosition position
) implements Expression {
}
