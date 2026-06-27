package error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorReporter {

    private final List<CompileError> errors = new ArrayList<>();

    public void report(SourcePosition position, String message) {
        errors.add(new CompileError(position, message));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<CompileError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void printErrors() {
        for (CompileError error : errors) {
            System.out.println(error);
        }
    }
}
