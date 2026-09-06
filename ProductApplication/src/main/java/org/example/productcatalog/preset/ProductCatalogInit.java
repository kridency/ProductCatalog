package org.example.productcatalog.preset;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ProductCatalogInit {
    public static final String EMAIL_ERROR = "Неправильный формат email";
    public static final String PRODUCT_NOT_CREATED = "Не удалось создать товар";
    public static final String PRODUCT_NOT_UPDATED =  "Не удалось изменить товар";
    public static final String PRODUCT_NOT_DELETED =  "Не удалось удалить товар";
    public static final String USER_NOT_CREATED = "Не удалось создать пользователя";
    public static final String USER_NOT_UPDATED =  "Не удалось изменить пользователя";
    public static final String USER_NOT_DELETED =  "Не удалось удалить пользователя";
    public static final String CREATED = "Объект успешно создан";
    public static final String UPDATED = "Объект успешно изменен";
    public static final String DELETED = "Объект успешно удален";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String USER_NOT_SPECIFIED = "Не указан Пользователь";
    public static final String EMAIL_NOT_SPECIFIED = "Не указан адрес электронной почты пользователя";
    public static final String PRODUCT_NOT_FOUND = "Товар не найден";
    public static final String PRODUCT_NOT_SPECIFIED = "Не указан товар";
    public static final DateTimeFormatter DATE_FORMAT;
    public static final DateTimeFormatter DATETIME_FORMATTER;
    public static final Scanner SCANNER;
    public static final ObjectMapper objectMapper;

    static {
        SCANNER = new Scanner(System.in);
        DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS");
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
