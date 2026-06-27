package ast;

import error.SourcePosition;

public record Label(
        String name,
        SourcePosition position
) implements Statement {
}
