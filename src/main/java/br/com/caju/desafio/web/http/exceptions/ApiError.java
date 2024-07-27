package br.com.caju.desafio.web.http.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;


@Getter
@Setter
public class ApiError {

    private final String message;
    private List<String> errors;
    private HashMap<String, String> fieldErrors = new HashMap<>();

    public ApiError(String message, List<String> errors, HashMap<String, String> fieldErrors) {
        super();
        this.message = message;
        this.errors = errors;
        this.fieldErrors.putAll(fieldErrors);
    }
}