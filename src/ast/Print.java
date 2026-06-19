package ast;

public record Print(
        Expression expression
) implements Statement {
}
