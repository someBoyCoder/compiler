package semantic;

import ast.*;
import error.ErrorReporter;

import java.util.*;

/**
 * Проверяет смысл кода
 */
public class SemanticAnalyzer {

    private final Map<String, Type> variables = new HashMap<>();

    private final Set<String> labels = new HashSet<>();

    private int breakDepth = 0;

    private final ErrorReporter errorReporter;

    public SemanticAnalyzer(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public void analyze(Program program) {
        collectLabels(program.statements());

        for (Statement statement : program.statements()) {
            analyzeStatement(statement);
        }
    }

    private void collectLabels(List<Statement> statements) {
        for (Statement statement : statements) {
            if (statement instanceof Label label) {
                if (labels.contains(label.name())) {
                    errorReporter.report(
                            label.position(),
                            "Метка уже объявлена: " + label.name()
                    );
                    continue;
                }

                labels.add(label.name());
            } else if (statement instanceof DoWhile doWhile) {
                collectLabels(doWhile.body());
            } else if (statement instanceof For forStatement) {
                collectLabels(forStatement.body());
            } else if (statement instanceof Switch switchStatement) {
                for (SwitchCase switchCase : switchStatement.cases()) {
                    collectLabels(switchCase.body());
                }

                if (switchStatement.defaultBody() != null) {
                    collectLabels(switchStatement.defaultBody());
                }
            }
        }
    }

    private void analyzeStatement(Statement statement) {
        if (statement instanceof Declaration declaration) {
            analyzeDeclaration(declaration);
        } else if (statement instanceof Assignment assignment) {
            analyzeAssignment(assignment);
        } else if (statement instanceof Print print) {
            analyzeExpression(print.expression());
        } else if (statement instanceof Input input) {
            analyzeInput(input);
        } else if (statement instanceof DoWhile doWhile) {
            analyzeDoWhile(doWhile);
        } else if (statement instanceof For forStatement) {
            analyzeFor(forStatement);
        } else if (statement instanceof Switch switchStatement) {
            analyzeSwitch(switchStatement);
        } else if (statement instanceof Break breakStatement) {
            analyzeBreak(breakStatement);
        } else if (statement instanceof Gosub gosub) {
            analyzeGosub(gosub);
        } else if (statement instanceof Return
                || statement instanceof Label
                || statement instanceof End) {
            // Эти операторы допустимы, дополнительная семантическая проверка не требуется
        } else {
            errorReporter.report(
                    statement.position(),
                    "Неизвестный оператор: " + statement.getClass().getSimpleName()
            );
        }
    }

    private void analyzeDeclaration(Declaration declaration) {
        if (variables.containsKey(declaration.name())) {
            errorReporter.report(
                    declaration.position(),
                    "Переменная уже объявлена: " + declaration.name()
            );
            return;
        }

        variables.put(declaration.name(), declaration.type());
    }

    private void analyzeAssignment(Assignment assignment) {
        if (!variables.containsKey(assignment.name())) {
            errorReporter.report(
                    assignment.position(),
                    "Переменная не объявлена: " + assignment.name()
            );
            analyzeExpression(assignment.expression());
            return;
        }

        Type variableType = variables.get(assignment.name());
        Type expressionType = analyzeExpression(assignment.expression());

        if (expressionType == Type.ERROR) {
            return;
        }

        if (variableType == expressionType) {
            return;
        }

        if (variableType == Type.DOUBLE && expressionType == Type.INT) {
            return;
        }

        errorReporter.report(
                assignment.position(),
                "Нельзя присвоить значение типа " + expressionType
                        + " переменной типа " + variableType
        );
    }

    private Type analyzeExpression(Expression expression) {
        if (expression instanceof NumberExpression) {
            return Type.INT;
        }

        if (expression instanceof DoubleExpression) {
            return Type.DOUBLE;
        }

        if (expression instanceof BooleanExpression) {
            return Type.BOOLEAN;
        }

        if (expression instanceof StringExpression) {
            return Type.STRING;
        }

        if (expression instanceof VariableExpression variable) {
            if (!variables.containsKey(variable.name())) {
                errorReporter.report(
                        variable.position(),
                        "Переменная не объявлена: " + variable.name()
                );
                return Type.ERROR;
            }

            return variables.get(variable.name());
        }

        if (expression instanceof BinaryExpression binary) {
            Type leftType = analyzeExpression(binary.left());
            Type rightType = analyzeExpression(binary.right());

            if (leftType == Type.ERROR || rightType == Type.ERROR) {
                return Type.ERROR;
            }

            String operator = binary.operator();

            if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
                if (!isNumeric(leftType) || !isNumeric(rightType)) {
                    errorReporter.report(
                            binary.position(),
                            "Арифметические операции допустимы только для int и double"
                    );
                    return Type.ERROR;
                }

                return numericResultType(leftType, rightType);
            }

            if (operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
                if (!isNumeric(leftType) || !isNumeric(rightType)) {
                    errorReporter.report(
                            binary.position(),
                            "Операции сравнения < <= > >= допустимы только для int и double"
                    );
                    return Type.ERROR;
                }

                return Type.BOOLEAN;
            }

            if (operator.equals("==") || operator.equals("!=")) {
                if (leftType == rightType) {
                    return Type.BOOLEAN;
                }

                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return Type.BOOLEAN;
                }

                errorReporter.report(
                        binary.position(),
                        "В операциях == и != типы должны совпадать"
                );
                return Type.ERROR;
            }
        }

        errorReporter.report(
                expression.position(),
                "Неизвестное выражение"
        );

        return Type.ERROR;
    }

    private void analyzeDoWhile(DoWhile doWhile) {
        for (Statement statement : doWhile.body()) {
            analyzeStatement(statement);
        }

        Type conditionType = analyzeExpression(doWhile.condition());

        if (conditionType != Type.ERROR && conditionType != Type.BOOLEAN) {
            errorReporter.report(
                    doWhile.condition().position(),
                    "Условие цикла do-while должно иметь тип boolean"
            );
        }
    }

    private void analyzeFor(For forStatement) {
        analyzeAssignment(forStatement.init());

        Type conditionType = analyzeExpression(forStatement.condition());

        if (conditionType != Type.ERROR && conditionType != Type.BOOLEAN) {
            errorReporter.report(
                    forStatement.condition().position(),
                    "Условие цикла for должно иметь тип boolean"
            );
        }

        analyzeAssignment(forStatement.update());

        for (Statement statement : forStatement.body()) {
            analyzeStatement(statement);
        }
    }

    private void analyzeSwitch(Switch switchStatement) {
        Type switchType = analyzeExpression(switchStatement.expression());

        breakDepth++;

        for (SwitchCase switchCase : switchStatement.cases()) {
            Type caseType = analyzeExpression(switchCase.value());

            if (caseType != Type.ERROR && switchType != Type.ERROR && caseType != switchType) {
                errorReporter.report(
                        switchCase.value().position(),
                        "Тип case должен совпадать с типом выражения switch"
                );
            }

            for (Statement statement : switchCase.body()) {
                analyzeStatement(statement);
            }
        }

        if (switchStatement.defaultBody() != null) {
            for (Statement statement : switchStatement.defaultBody()) {
                analyzeStatement(statement);
            }
        }

        breakDepth--;
    }

    private void analyzeBreak(Break breakStatement) {
        if (breakDepth == 0) {
            errorReporter.report(
                    breakStatement.position(),
                    "Оператор break можно использовать только внутри switch"
            );
        }
    }

    private void analyzeInput(Input input) {
        if (!variables.containsKey(input.variableName())) {
            errorReporter.report(
                    input.position(),
                    "Переменная не объявлена: " + input.variableName()
            );
        }
    }

    private void analyzeGosub(Gosub gosub) {
        if (!labels.contains(gosub.labelName())) {
            errorReporter.report(
                    gosub.position(),
                    "Метка не найдена: " + gosub.labelName()
            );
        }
    }

    private Type numericResultType(Type leftType, Type rightType) {
        if (leftType == Type.DOUBLE || rightType == Type.DOUBLE) {
            return Type.DOUBLE;
        }

        return Type.INT;
    }

    private boolean isNumeric(Type type) {
        return type == Type.INT || type == Type.DOUBLE;
    }
}
