package org.example.productcatalog.preset;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ProductCatalogInit {
    public static final String UNAUTHORIZED;
    public static final String RETURN;
    public static final String INPUT_ERROR;
    public static final String EMAIL_ERROR = "Неправильный формат email";
    public static final String COMMAND_PROMPT;
    public static final String FORCED_COMPLETION;
    public static final String USER_NOT_FOUND;
    public static final String USER_NOT_SPECIFIED;
    public static final String EMAIL_NOT_SPECIFIED;
    public static final String PRODUCT_NOT_FOUND;
    public static final String PRODUCT_NOT_SPECIFIED;
    public static final String LIMIT_NOT_FOUND;
    public static final String FUND_NOT_FOUND;
    public static final DateTimeFormatter DATE_FORMAT;
    public static final DateTimeFormatter DATETIME_FORMATTER;
    public static final String BAD_REQUEST;
    public static final String BAD_ENDPOINT;
    public static final Scanner SCANNER;
    public static final ObjectMapper objectMapper;

    static {
        UNAUTHORIZED = "Необходимо пройти аутентификацию";
        RETURN = "return";
        INPUT_ERROR = "Неопознанная команда";
        //EMAIL_ERROR = "Неправильный формат email";
        COMMAND_PROMPT = System.lineSeparator() + "Введите команду :> ";
        FORCED_COMPLETION =  "Принудительное завершение" + System.lineSeparator();
        USER_NOT_FOUND = "Пользователь не найден";
        USER_NOT_SPECIFIED = "Не указан Пользователь";
        EMAIL_NOT_SPECIFIED = "Не указан адрес электронной почты пользователя";
        PRODUCT_NOT_FOUND = "Товар не найден";
        PRODUCT_NOT_SPECIFIED = "Не указан товар";
        LIMIT_NOT_FOUND = "Лимит не найден";
        FUND_NOT_FOUND = "Фонд накоплений не найден";
        BAD_ENDPOINT = "Недопустимая операция";
        BAD_REQUEST = "Входящие данные не соответствуют формату";
        SCANNER = new Scanner(System.in);
        DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS");
        objectMapper = new ObjectMapper();
    }
}
