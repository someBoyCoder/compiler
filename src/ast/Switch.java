package ast;

import error.SourcePosition;

import java.util.List;

public record Switch(
        Expression expression,
        List<SwitchCase> cases,
        List<Statement> defaultBody,
        SourcePosition position
) implements Statement {
}
