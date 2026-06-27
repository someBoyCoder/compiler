# Документация по синтаксису учебного языка программирования

## 1. Общая информация

В рамках проекта реализован учебный язык программирования, похожий на упрощённый Java/BASIC. Язык поддерживает переменные, арифметические и логические выражения, ввод и вывод данных, циклы, оператор выбора `switch`, а также вызов подпрограммы через `gosub`.

Программа состоит из последовательности операторов. Каждый оператор, кроме метки, завершается символом `;`.

Пример программы:

```java
int x;
double y;
string name;
boolean flag;

x = 5;
y = 2.5;
name = "Alex";
flag = x < 10;

print name;
print x + y;
print flag;
```

## 2. Типы данных

Язык поддерживает четыре основных типа данных:

```java
int
double
boolean
string
```

### `int`

Тип `int` используется для целых чисел.

```java
int x;
x = 10;
```

### `double`

Тип `double` используется для вещественных чисел.

```java
double price;
price = 15.5;
```

Переменной типа `double` можно присвоить значение типа `int`:

```java
double x;
x = 5;
```

Но переменной типа `int` нельзя присвоить `double`:

```java
int x;
x = 2.5; // ошибка
```

### `boolean`

Тип `boolean` хранит логические значения:

```java
boolean flag;
flag = true;
flag = false;
```

### `string`

Тип `string` хранит строковые значения.

```java
string name;
name = "Anna";
```

Строки записываются в двойных кавычках.

## 3. Объявление переменных

Перед использованием переменная должна быть объявлена.

Формат объявления:

```java
тип имя;
```

Примеры:

```java
int count;
double sum;
boolean isReady;
string text;
```

Повторное объявление переменной с тем же именем считается ошибкой:

```java
int x;
int x; // ошибка
```

## 4. Присваивание

Формат присваивания:

```java
имя = выражение;
```

Примеры:

```java
int x;
x = 5;

double y;
y = 2.5;

boolean flag;
flag = x < 10;

string text;
text = "Hello";
```

Тип значения должен быть совместим с типом переменной.

Пример ошибки:

```java
int x;
x = "hello"; // ошибка: нельзя присвоить string переменной int
```

## 5. Выражения

Язык поддерживает арифметические выражения:

```java
+
-
*
/
```

Примеры:

```java
int x;
x = 2 + 3 * 4;

double y;
y = 10.5 / 2;
```

Приоритет операций стандартный:

```java
* и / выполняются раньше + и -
```

Скобки можно использовать для изменения порядка вычислений:

```java
x = (2 + 3) * 4;
```

## 6. Операции сравнения

Язык поддерживает операции сравнения:

```java
<
<=
>
>=
==
!=
```

Примеры:

```java
boolean result;

result = 5 < 10;
result = 10 >= 3;
result = 5 == 5;
result = 5 != 3;
```

Операции `<`, `<=`, `>`, `>=` применяются только к числовым типам `int` и `double`.

Операции `==` и `!=` можно использовать для сравнения значений совместимых типов.

## 7. Вывод данных: `print`

Оператор `print` выводит значение выражения на экран.

Формат:

```java
print выражение;
```

Примеры:

```java
print 10;
print 2 + 3;
print "Hello";
print true;
print x;
```

Можно выводить переменные:

```java
string name;
name = "Anna";

print name;
```

## 8. Ввод данных: `input`

Оператор `input` считывает значение из консоли и сохраняет его в переменную.

Формат:

```java
input имяПеременной;
```

Пример:

```java
string name;
int age;

print "Введите имя:";
input name;

print "Введите возраст:";
input age;

print name;
print age;
```

Переменная должна быть заранее объявлена:

```java
input x; // ошибка, если x не объявлена
```

## 9. Цикл с постусловием `do-while`

Цикл `do-while` выполняет тело цикла минимум один раз, а затем проверяет условие.

Формат:

```java
do {
    операторы
} while (условие);
```

Пример:

```java
int x;
x = 0;

do {
    x = x + 1;
    print x;
} while (x < 3);
```

Результат:

```text
1
2
3
```

Условие цикла должно иметь тип `boolean`.

Пример ошибки:

```java
int x;
x = 0;

do {
    x = x + 1;
} while (x); // ошибка, x имеет тип int, а нужно boolean
```

## 10. Цикл `for`

Цикл `for` используется, когда нужно выполнить блок кода несколько раз.

Формат:

```java
for (инициализация; условие; обновление) {
    операторы
}
```

Пример:

```java
int i;

for (i = 0; i < 3; i = i + 1) {
    print i;
}
```

Результат:

```text
0
1
2
```

В текущей версии языка переменная цикла должна быть объявлена заранее:

```java
int i;

for (i = 0; i < 5; i = i + 1) {
    print i;
}
```

Такой вариант не поддерживается:

```java
for (int i = 0; i < 5; i = i + 1) {
    print i;
}
```

Условие цикла `for` должно иметь тип `boolean`.

## 11. Оператор выбора `switch`

Оператор `switch` выбирает один из блоков `case` в зависимости от значения выражения.

Формат:

```java
switch (выражение) {
    case значение:
        операторы
        break;
    default:
        операторы
        break;
}
```

Пример:

```java
int x;

x = 2;

switch (x) {
    case 1:
        print 100;
        break;
    case 2:
        print 200;
        break;
    default:
        print 0;
        break;
}
```

Результат:

```text
200
```

Тип значения в `case` должен совпадать с типом выражения в `switch`.

Пример ошибки:

```java
int x;
x = 1;

switch (x) {
    case true:
        print 100;
        break;
}
```

В этом примере `x` имеет тип `int`, а `true` имеет тип `boolean`, поэтому возникает ошибка типов.

## 12. Оператор `break`

Оператор `break` используется для выхода из блока `switch`.

Формат:

```java
break;
```

Пример:

```java
switch (x) {
    case 1:
        print 1;
        break;
}
```

Использование `break` вне `switch` считается ошибкой:

```java
break; // ошибка
```

## 13. Метки

Метка обозначает место в программе, куда можно перейти с помощью `gosub`.

Формат:

```java
имяМетки:
```

Пример:

```java
hello:
    print "Hello";
    return;
```

Имена меток не должны повторяться.

Пример ошибки:

```java
hello:
    print "one";

hello:
    print "two";
```

## 14. Вызов подпрограммы `gosub`

Оператор `gosub` вызывает блок кода, расположенный по метке.

Формат:

```java
gosub имяМетки;
```

Пример:

```java
print "Начало";

gosub hello;

print "Конец";

end;

hello:
    print "Привет из gosub";
    return;
```

Результат:

```text
Начало
Привет из gosub
Конец
```

Если метка не найдена, возникает семантическая ошибка:

```java
gosub unknown; // ошибка
```

## 15. Оператор `return`

Оператор `return` завершает выполнение подпрограммы, вызванной через `gosub`, и возвращает выполнение к следующей инструкции после `gosub`.

Формат:

```java
return;
```

Пример:

```java
hello:
    print "Hello";
    return;
```

## 16. Оператор `end`

Оператор `end` завершает выполнение программы.

Формат:

```java
end;
```

Обычно `end` используется перед блоком меток, чтобы основной поток выполнения случайно не начал выполнять код подпрограммы.

Пример:

```java
print "Основная программа";

gosub procedure;

print "После вызова";

end;

procedure:
    print "Подпрограмма";
    return;
```

## 17. Обработка ошибок

В языке реализована обработка ошибок с указанием строки и колонки.

Пример программы с ошибками:

```java
int x;
int x;

y = 5;

boolean flag;
flag = 10;

int z;
z = "hello";

do {
    print z;
} while (z);
```

Пример вывода ошибок:

```text
Ошибка [2:5]: Переменная уже объявлена: x
Ошибка [4:1]: Переменная не объявлена: y
Ошибка [7:1]: Нельзя присвоить значение типа INT переменной типа BOOLEAN
Ошибка [10:1]: Нельзя присвоить значение типа STRING переменной типа INT
Ошибка [14:10]: Условие цикла do-while должно иметь тип boolean
```

Если ошибки найдены, генерация кода и выполнение программы не запускаются.

## 18. Краткая грамматика языка

Ниже приведено упрощённое описание грамматики языка:

```bnf
<program> ::= <statement>* EOF

<statement> ::= <declaration>
              | <assignment>
              | <print>
              | <input>
              | <doWhile>
              | <for>
              | <switch>
              | <break>
              | <gosub>
              | <return>
              | <end>
              | <label>

<declaration> ::= <type> IDENTIFIER ";"

<type> ::= "int"
         | "double"
         | "boolean"
         | "string"

<assignment> ::= IDENTIFIER "=" <expression> ";"

<print> ::= "print" <expression> ";"

<input> ::= "input" IDENTIFIER ";"

<doWhile> ::= "do" "{" <statement>* "}" "while" "(" <expression> ")" ";"

<for> ::= "for" "(" <assignmentWithoutSemicolon> ";" <expression> ";" <assignmentWithoutSemicolon> ")" "{" <statement>* "}"

<switch> ::= "switch" "(" <expression> ")" "{" <case>* <default>? "}"

<case> ::= "case" <expression> ":" <statement>*

<default> ::= "default" ":" <statement>*

<break> ::= "break" ";"

<gosub> ::= "gosub" IDENTIFIER ";"

<return> ::= "return" ";"

<end> ::= "end" ";"

<label> ::= IDENTIFIER ":"

<expression> ::= <equality>

<equality> ::= <comparison> (("==" | "!=") <comparison>)*

<comparison> ::= <term> (("<" | "<=" | ">" | ">=") <term>)*

<term> ::= <factor> (("+" | "-") <factor>)*

<factor> ::= <primary> (("*" | "/") <primary>)*

<primary> ::= NUMBER
            | STRING
            | "true"
            | "false"
            | IDENTIFIER
            | "(" <expression> ")"
```

## 19. Пример полной программы

```java
string name;
int i;
double result;
boolean flag;

print "Введите имя:";
input name;

result = 2.5 + 5;
flag = result > 5;

print "Имя:";
print name;

print "Результат:";
print result;

for (i = 0; i < 3; i = i + 1) {
    print i;
}

switch (i) {
    case 3:
        print "Цикл завершён";
        break;
    default:
        print "Другое значение";
        break;
}

gosub hello;

end;

hello:
    print "Подпрограмма выполнена";
    return;
```
