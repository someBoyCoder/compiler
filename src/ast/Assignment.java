package ast;

public record Assignment(
        String name,
        Expression expression
) implements Statement {
}
