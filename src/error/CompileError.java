package error;

public record CompileError(
        SourcePosition position,
        String message
) {
    @Override
    public String toString() {
        return "Ошибка " + position + ": " + message;
    }
}
