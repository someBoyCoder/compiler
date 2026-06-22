package ast;

import java.util.List;

public record Switch(
        Expression expression,
        List<SwitchCase> cases,
        List<Statement> defaultBody
) implements Statement {
}
