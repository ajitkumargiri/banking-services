package com.ajit.account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * The type Error.
 */
@Getter
@Setter
@AllArgsConstructor
public class Error {
    private int statusCode;
    private Date timestamp;
    private String title;
    private String detail;
}