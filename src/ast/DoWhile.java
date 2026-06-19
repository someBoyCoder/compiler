package ast;

import java.util.List;

public record DoWhile(
        List<Statement> body,
        Expression condition
) implements Statement {
}
