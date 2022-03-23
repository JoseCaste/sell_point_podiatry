package com.podiatry.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateException extends Exception{

    private String message;

}
