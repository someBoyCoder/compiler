package ast;

import java.util.List;

public record Program(
        List<Statement> statements
) {
}
