package com.fedorov.storage.controllers;


import com.fedorov.storage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("path") String path) {

        return fileService.upload(multipartFile, path);

    }

    @PostMapping("/createDirectory")
    public ResponseEntity<?> createDirectory(@RequestParam("name") String name, @RequestParam("path") String path) {

        return fileService.createDirectory(name, path);

    }

    @PostMapping("/deleteFile")
    public ResponseEntity<?> deleteFile(@RequestParam("name") String name, @RequestParam("path") String path) {
        return fileService.deleteFile(path, name);
    }

    @PostMapping("/deleteDirectory")
    public ResponseEntity<?> deleteDirectory(@RequestParam("name") String name, @RequestParam("path") String path) {
        return fileService.deleteDirectory(path, name);
    }

}
