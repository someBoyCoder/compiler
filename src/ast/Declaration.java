package ast;

import error.SourcePosition;
import semantic.Type;

public record Declaration(
        Type type,
        String name,
        SourcePosition position
) implements Statement {
}
