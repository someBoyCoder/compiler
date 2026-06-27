package ast;

import error.SourcePosition;

public record Return(
        SourcePosition position
) implements Statement {
}
