package com.fedorov.storage.services;


import com.fedorov.storage.models.FileModel;
import com.fedorov.storage.repo.FileRepo;
import com.fedorov.storage.utils.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class FileService {
    @Autowired
    private FileRepo fileRepo;

    @Transactional
    public ResponseEntity<?> upload(MultipartFile multipartFile, String path) {


        String filePath = path + "/" + multipartFile.getOriginalFilename();

        FileModel fileModel = new FileModel();

        fileModel.setName(multipartFile.getOriginalFilename());
        fileModel.setExtension(multipartFile.getContentType());
        fileModel.setPath(path);
        fileModel.setDate(LocalDateTime.now());
        fileModel.setSize((int) multipartFile.getSize());


        System.out.println(multipartFile.getOriginalFilename() + " " + "name");
        System.out.println(multipartFile.getContentType() + " " + "extension");
        System.out.println(filePath + " " + "path");
        System.out.println(LocalDateTime.now() + " " + "date");
        System.out.println(multipartFile.getSize() + " " + "size");

        if (fileRepo.findByNameAndPath(multipartFile.getOriginalFilename(), path).isPresent()) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        else {
            try {
                multipartFile.transferTo(new File(filePath));
                fileRepo.save(fileModel);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
    @Transactional
    public ResponseEntity<?> createDirectory(String name, String path) {

        File directory = new File(path + "/" + name);

        if (!directory.isDirectory()) {

            directory.mkdir();


        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);


    }
    @Transactional
    public ResponseEntity<?> deleteFile(String name, String path) {
        File file = new File(path + "/" + name);
        Delete delete = new Delete();
        if (file.delete()) {

            fileRepo.deleteByNameAndPath(name, path);
            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    @Transactional
    public ResponseEntity<?> deleteDirectory(String name, String path) {

        Delete delete = new Delete();
        if (delete.deleteDirectory(name, path)) {

            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



    }
}
