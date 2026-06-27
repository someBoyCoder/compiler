package ast;

import error.SourcePosition;

public record Assignment(
        String name,
        Expression expression,
        SourcePosition position
) implements Statement {
}
