package ast;

import error.SourcePosition;

public record Input(
        String variableName,
        SourcePosition position
) implements Statement {
}
