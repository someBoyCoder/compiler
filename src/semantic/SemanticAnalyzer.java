package semantic;

import ast.*;

import java.util.*;

/**
 * Проверяет смысл кода
 */
public class SemanticAnalyzer {

    private final Map<String, Type> variables = new HashMap<>();

    private final Set<String> labels = new HashSet<>();

    private int breakDepth = 0;

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
                    throw new RuntimeException("Метка уже объявлена: " + label.name());
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
        } else if (statement instanceof Break) {
            analyzeBreak();
        } else if (statement instanceof Gosub gosub) {
            analyzeGosub(gosub);
        } else if (statement instanceof Return) {
            // return допустим, отдельной проверки пока не делаем
        } else if (statement instanceof Label) {
            // метки уже проверены в collectLabels
        } else if (statement instanceof End) {
            // end допустим
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

        if (variableType == expressionType) {
            return;
        }

        if (variableType == Type.DOUBLE && expressionType == Type.INT) {
            return;
        }

        throw new RuntimeException("Нельзя присвоить значение типа "
                + expressionType + " переменной типа " + variableType);
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
                throw new RuntimeException("Переменная не объявлена: " + variable.name());
            }

            return variables.get(variable.name());
        }

        if (expression instanceof BinaryExpression binary) {
            Type leftType = analyzeExpression(binary.left());
            Type rightType = analyzeExpression(binary.right());

            String operator = binary.operator();

            if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
                if (!isNumeric(leftType) || !isNumeric(rightType)) {
                    throw new RuntimeException("Арифметические операции допустимы только для int и double");
                }

                return numericResultType(leftType, rightType);
            }

            if (operator.equals("<") || operator.equals("<=") || operator.equals(">") || operator.equals(">=")) {
                if (!isNumeric(leftType) || !isNumeric(rightType)) {
                    throw new RuntimeException("Операции сравнения < <= > >= допустимы только для int и double");
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

                throw new RuntimeException("В операциях == и != типы должны совпадать");
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

    private void analyzeSwitch(Switch switchStatement) {
        Type switchType = analyzeExpression(switchStatement.expression());

        breakDepth++;

        for (SwitchCase switchCase : switchStatement.cases()) {
            if (!(switchCase.value() instanceof NumberExpression)
                    && !(switchCase.value() instanceof BooleanExpression)) {
                throw new RuntimeException("Значение case должно быть литералом");
            }

            Type caseType = analyzeExpression(switchCase.value());

            if (caseType != switchType) {
                throw new RuntimeException("Тип case должен совпадать с типом выражения switch");
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

    private void analyzeBreak() {
        if (breakDepth == 0) {
            throw new RuntimeException("Оператор break можно использовать только внутри switch");
        }
    }

    private void analyzeInput(Input input) {
        if (!variables.containsKey(input.variableName())) {
            throw new RuntimeException("Переменная не объявлена: " + input.variableName());
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

    private void analyzeGosub(Gosub gosub) {
        if (!labels.contains(gosub.labelName())) {
            throw new RuntimeException("Метка не найдена: " + gosub.labelName());
        }
    }
}
