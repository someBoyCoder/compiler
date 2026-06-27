package ast;

import error.SourcePosition;

public record End(
        SourcePosition position
) implements Statement {
}
