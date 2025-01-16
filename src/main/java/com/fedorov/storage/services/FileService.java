package com.fedorov.storage.services;


import com.fedorov.storage.models.FileModel;
import com.fedorov.storage.repo.FileRepo;
import com.fedorov.storage.utils.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class FileService {
    @Autowired
    private FileRepo fileRepo;

    @Transactional
    public ResponseEntity<?> upload(MultipartFile multipartFile, String path) {


        String filePath = path + "/" + multipartFile.getOriginalFilename();

        FileModel fileModel = new FileModel();
        String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".") + 1);
        fileModel.setName(multipartFile.getOriginalFilename());
        fileModel.setExtension(extension);
        fileModel.setPath(path);
        fileModel.setDate(LocalDateTime.now());
        float scale = (float) Math.pow(10, 5);
        fileModel.setSize((Math.round(((float) multipartFile.getSize() / (1024 * 1024)) * scale) / scale));



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

            FileModel fileModel = new FileModel();

            fileModel.setName(name);
            fileModel.setExtension("directory");
            fileModel.setPath(path);
            fileModel.setDate(LocalDateTime.now());
            fileRepo.save(fileModel);


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

    @Transactional
    public ResponseEntity<?> getFiles(String path) {


        return new ResponseEntity<>(fileRepo.findAllByPath(path), HttpStatus.OK);

    }


    public ResponseEntity<?> getFilesOrderByName(String path, String order) {


        return switch (order) {
            case "ASC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByNameAsc(path), HttpStatus.OK);

            case "DESC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByNameDesc(path), HttpStatus.OK);

            default -> new ResponseEntity<>("Значение сортировки должно быть ASC или DESC", HttpStatus.BAD_REQUEST);
        };


    }

    public ResponseEntity<?> getFilesOrderByExtension(String path, String order) {



        return switch (order) {
            case "ASC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByExtensionAsc(path), HttpStatus.OK);

            case "DESC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByExtensionDesc(path), HttpStatus.OK);

            default -> new ResponseEntity<>("Значение сортировки должно быть ASC или DESC", HttpStatus.BAD_REQUEST);
        };


    }

    public ResponseEntity<?> getFilesOrderBySize(String path, String order) {



        return switch (order) {
            case "ASC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderBySizeAsc(path), HttpStatus.OK);

            case "DESC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderBySizeDesc(path), HttpStatus.OK);

            default -> new ResponseEntity<>("Значение сортировки должно быть ASC или DESC", HttpStatus.BAD_REQUEST);
        };


    }

    public ResponseEntity<?> getFilesOrderByDate(String path, String order) {



        return switch (order) {
            case "ASC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByDateAsc(path), HttpStatus.OK);

            case "DESC" -> new ResponseEntity<>(fileRepo.findAllByPathOrderByDateDesc(path), HttpStatus.OK);

            default -> new ResponseEntity<>("Значение сортировки должно быть ASC или DESC", HttpStatus.BAD_REQUEST);
        };


    }

    public ResponseEntity<FileSystemResource> downloadFile(String path, String name) {

        File file = new File(Paths.get(path, name).toString());
        if (file.isFile()) {
            FileSystemResource resource = new FileSystemResource(file);
            String mimeType = "application/octet-stream";
            try {

                Path filePath = Paths.get(file.getAbsolutePath());
                mimeType = Files.probeContentType(filePath);

            } catch (IOException e) {


            }


            System.out.println(file.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8))
                    .body(resource);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

}
