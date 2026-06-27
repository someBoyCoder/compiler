package ast;

import error.SourcePosition;

import java.util.List;

public record DoWhile(
        List<Statement> body,
        Expression condition,
        SourcePosition position
) implements Statement {
}
