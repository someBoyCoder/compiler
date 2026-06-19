package semantic;

import ast.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Проверяет смысл кода
 */
public class SemanticAnalyzer {

    private final Map<String, Type> variables = new HashMap<>();

    public void analyze(Program program) {
        for (Statement statement : program.statements()) {
            analyzeStatement(statement);
        }
    }

    private void analyzeStatement(Statement statement) {
        if (statement instanceof Declaration declaration) {
            analyzeDeclaration(declaration);
        } else if (statement instanceof Assignment assignment) {
            analyzeAssignment(assignment);
        } else if (statement instanceof Print print) {
            analyzeExpression(print.expression());
        } else if (statement instanceof DoWhile doWhile) {
            analyzeDoWhile(doWhile);
        } else if (statement instanceof For forStatement) {
            analyzeFor(forStatement);
        }
    }

    private void analyzeDeclaration(Declaration declaration) {
        if (variables.containsKey(declaration.name())) {
            throw new RuntimeException("Переменная уже объявлена: " + declaration.name());
        }

        variables.put(declaration.name(), declaration.type());
    }

    private void analyzeAssignment(Assignment assignment) {
        if (!variables.containsKey(assignment.name())) {
            throw new RuntimeException("Переменная не объявлена: " + assignment.name());
        }

        Type variableType = variables.get(assignment.name());
        Type expressionType = analyzeExpression(assignment.expression());

        if (variableType != expressionType) {
            throw new RuntimeException("Несовместимые типы при присваивании");
        }
    }

    private Type analyzeExpression(Expression expression) {
        if (expression instanceof NumberExpression) {
            return Type.INT;
        }

        if (expression instanceof BooleanExpression) {
            return Type.BOOLEAN;
        }

        if (expression instanceof VariableExpression variable) {
            if (!variables.containsKey(variable.name())) {
                throw new RuntimeException("Переменная не объявлена: " + variable.name());
            }

            return variables.get(variable.name());
        }

        if (expression instanceof BinaryExpression binary) {
            Type leftType = analyzeExpression(binary.left());
            Type rightType = analyzeExpression(binary.right());

            String operator = binary.operator();

            if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
                if (leftType != Type.INT || rightType != Type.INT) {
                    throw new RuntimeException("Арифметические операции допустимы только для int");
                }

                return Type.INT;
            }

            if (operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
                if (leftType != Type.INT || rightType != Type.INT) {
                    throw new RuntimeException("Операции сравнения < <= > >= допустимы только для int");
                }

                return Type.BOOLEAN;
            }

            if (operator.equals("==") || operator.equals("!=")) {
                if (leftType != rightType) {
                    throw new RuntimeException("В операциях == и != типы должны совпадать");
                }

                return Type.BOOLEAN;
            }
        }

        throw new RuntimeException("Неизвестное выражение");
    }

    private void analyzeDoWhile(DoWhile doWhile) {
        for (Statement statement : doWhile.body()) {
            analyzeStatement(statement);
        }

        Type conditionType = analyzeExpression(doWhile.condition());

        if (conditionType != Type.BOOLEAN) {
            throw new RuntimeException("Условие цикла do-while должно иметь тип boolean");
        }
    }

    private void analyzeFor(For forStatement) {
        analyzeAssignment(forStatement.init());

        Type conditionType = analyzeExpression(forStatement.condition());

        if (conditionType != Type.BOOLEAN) {
            throw new RuntimeException("Условие цикла for должно иметь тип boolean");
        }

        analyzeAssignment(forStatement.update());

        for (Statement statement : forStatement.body()) {
            analyzeStatement(statement);
        }
    }
}
