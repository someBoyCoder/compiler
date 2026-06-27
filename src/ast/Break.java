package ast;

import error.SourcePosition;

public record Break(
        SourcePosition position
) implements Statement {
}
