package ast;

import error.SourcePosition;

public record Gosub(
        String labelName,
        SourcePosition position
) implements Statement {
}
