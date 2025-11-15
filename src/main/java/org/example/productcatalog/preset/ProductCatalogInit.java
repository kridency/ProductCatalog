package org.example.productcatalog.preset;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Scanner;

public class ProductCatalogInit {
    public static final String UNAUTHORIZED;
    public static final String RETURN;
    public static final String INPUT_ERROR;
    public static final String EMAIL_ERROR;
    public static final String COMMAND_PROMPT;
    public static final String FORCED_COMPLETION;
    public static final String USER_NOT_FOUND;
    public static final String PRODUCT_NOT_FOUND;
    public static final String LIMIT_NOT_FOUND;
    public static final String FUND_NOT_FOUND;
    public static final DateTimeFormatter DATE_FORMAT;
    public static final String BAD_REQUEST;
    public static final String BAD_ENDPOINT;
    public static final Scanner SCANNER;


    static {
        UNAUTHORIZED = "Необходимо пройти аутентификацию";
        RETURN = "return";
        INPUT_ERROR = "Неопознанная команда";
        EMAIL_ERROR = "Неправильный формат email";
        COMMAND_PROMPT = System.lineSeparator() + "Введите команду :> ";
        FORCED_COMPLETION =  "Принудительное завершение" + System.lineSeparator();
        USER_NOT_FOUND = "Пользователь не найден";
        PRODUCT_NOT_FOUND = "Товар не найден";
        LIMIT_NOT_FOUND = "Лимит не найден";
        FUND_NOT_FOUND = "Фонд накоплений не найден";
        DATE_FORMAT = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .toFormatter();
        BAD_ENDPOINT = "Недопустимая операция";
        BAD_REQUEST = "Входящие данные не соответствуют формату";
        SCANNER = new Scanner(System.in);
    }
}
