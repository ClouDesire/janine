package com.liberologico.janine;

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
        List<Object> arguments;
        String defaultMessage;
        String objectName;
        String field;
        List<Object> rejectedValue;
        Boolean bindingFailure;
        String code;

        @Override
        public String toString()
        {
            return "Error{" + "defaultMessage='" + defaultMessage + '\'' +
                            ", objectName='" + objectName + '\'' +
                            ", field='" + field + '\'' +
                            ", code='" + code + '\'' +
                            '}';
        }
    }

    @Override
    public String toString()
    {
        return "ApiError{" + "error='" + error + '\'' +
                           ", exception='" + exception + '\'' +
                           ", message='" + message + '\'' +
                           ", errors=" + errors +
                           '}';
    }
}
