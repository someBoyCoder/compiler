package ast;

import semantic.Type;

public record Declaration(
        Type type,
        String name
) implements Statement {
}
