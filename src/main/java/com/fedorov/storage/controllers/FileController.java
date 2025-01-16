package com.fedorov.storage.controllers;


import com.fedorov.storage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        return fileService.deleteFile(name, path);
    }

    @PostMapping("/deleteDirectory")
    public ResponseEntity<?> deleteDirectory(@RequestParam("name") String name, @RequestParam("path") String path) {
        return fileService.deleteDirectory(path, name);
    }



    @GetMapping("/getFiles")
    public ResponseEntity<?> getFiles(@RequestParam("path") String path) {


        return fileService.getFiles(path);

    }

    @GetMapping("/getFilesOrderByName")
    public ResponseEntity<?> getFilesOrderByName(@RequestParam("path") String path, @RequestParam("order") String order) {


        return fileService.getFilesOrderByName(path, order);

    }

    @GetMapping("/getFilesOrderByExtension")
    public ResponseEntity<?> getFilesOrderByExtension(@RequestParam("path") String path, @RequestParam("order") String order) {


        return fileService.getFilesOrderByExtension(path, order);

    }

    @GetMapping("/getFilesOrderBySize")
    public ResponseEntity<?> getFilesOrderBySize(@RequestParam("path") String path, @RequestParam("order") String order) {


        return fileService.getFilesOrderBySize(path, order);

    }

    @GetMapping("/getFilesOrderByDate")
    public ResponseEntity<?> getFilesOrderByDate(@RequestParam("path") String path, @RequestParam("order") String order) {


        return fileService.getFilesOrderByDate(path, order);

    }

    @GetMapping("/downloadFile")
    public ResponseEntity<?> download(@RequestParam("path") String path, @RequestParam("name") String name) {

        return fileService.downloadFile(path, name);

    }

}
