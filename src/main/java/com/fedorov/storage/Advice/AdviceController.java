package com.fedorov.storage.Advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdviceController {


    @ExceptionHandler
    public ResponseEntity<?> handlerUploadException(UploadException e) {

        CustomError error = new CustomError("Ошибка загрузки", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);


    }
    @ExceptionHandler
    public ResponseEntity<?> handlerUploadNameException(UploadNameException e) {

        CustomError error = new CustomError("Файл с таким именем уже существует", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler
    public ResponseEntity<?> handlerCreateDirectoryException(CreateDirectoryException e) {

        CustomError error = new CustomError("Файл с таким именем уже существует", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler
    public ResponseEntity<?> handlerCreateDirectoryNameException(CreateDirectoryNameException e) {

        CustomError error = new CustomError("Директория с таким именем уже существует", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler
    public ResponseEntity<?> handlerDeleteFileException(DeleteFileException e) {

        CustomError error = new CustomError("Не получается удалить файл", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

}
