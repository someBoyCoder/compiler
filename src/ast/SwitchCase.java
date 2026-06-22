package ast;

import java.util.List;

public record SwitchCase(
        Expression value,
        List<Statement> body
) {
}
