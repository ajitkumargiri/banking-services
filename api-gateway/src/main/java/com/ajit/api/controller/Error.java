package com.ajit.api.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Error {
    private int statusCode;
    private Date timestamp;
    private String title;
    private String detail;
}