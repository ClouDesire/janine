package com.liberologico.invoice_api;

import java.util.Date;
import java.util.List;

public class ApiError
{
    Date timestamp;
    Integer status;
    String error;
    String exception;
    List<Error> errors;
    String message;
    String path;

    class Error
    {
        List<String> codes;
        List<Argument> arguments;
        String defaultMessage;
        String objectName;
        String field;
        Object rejectedValue;
        Boolean bindingFailure;
        String code;

        class Argument
        {
            List<String> codes;
            Object arguments;
            String defaultMessage;
            String code;
        }
    }
}
