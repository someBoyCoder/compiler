package ast;

import error.SourcePosition;

public record Print(
        Expression expression,
        SourcePosition position
) implements Statement {
}
