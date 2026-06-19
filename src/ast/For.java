package ast;

import java.util.List;

public record For(
        Assignment init,
        Expression condition,
        Assignment update,
        List<Statement> body
) implements Statement {
}
