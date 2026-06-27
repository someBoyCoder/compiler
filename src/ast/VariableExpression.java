package ast;

import error.SourcePosition;

public record VariableExpression(
        String name,
        SourcePosition position
) implements Expression {
}
