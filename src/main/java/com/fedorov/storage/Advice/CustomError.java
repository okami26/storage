package com.fedorov.storage.Advice;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomError {

    private String Message;
    private int statusCode;

}
