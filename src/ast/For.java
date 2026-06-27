package ast;

import error.SourcePosition;

import java.util.List;

public record For(
        Assignment init,
        Expression condition,
        Assignment update,
        List<Statement> body,
        SourcePosition position
) implements Statement {
}
