package com.fedorov.storage.services;


import com.fedorov.storage.Advice.DeleteFileException;
import com.fedorov.storage.Advice.UploadException;
import com.fedorov.storage.Advice.UploadNameException;
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

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    @Autowired
    private FileRepo fileRepo;

    @Autowired
    private Delete delete;

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

            throw new UploadNameException();

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
    public ResponseEntity<?> uploadDirectory(MultipartFile multipartFile, String path) {

        String name = multipartFile.getOriginalFilename().substring(0, multipartFile.getOriginalFilename().lastIndexOf("."));

        String outputDir = path + "/" + name;

        try (ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream())) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {



                File outputFile = new File(outputDir, entry.getName());

                if (entry.isDirectory()) {
                    outputFile.mkdirs();



                } else {

                    outputFile.getParentFile().mkdirs();



                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();


            }
            File file = new File(outputDir);
            File[] files = file.listFiles();
            Arrays.stream(files).forEach(i -> {



                if (i.isDirectory()) {

                    FileModel fileModel = new FileModel();

                    System.out.println(i.getName());
                    fileModel.setName(i.getName());
                    fileModel.setExtension("directory");
                    fileModel.setPath(i.getParent());
                    fileModel.setDate(LocalDateTime.now());

                    fileRepo.save(fileModel);
                    System.out.println("Директория сохранена");

                    Arrays.stream(i.listFiles()).forEach(j -> {
                        FileModel fileModel1 = new FileModel();

                        String extension = j.getName().substring(j.getName().indexOf(".") + 1);
                        fileModel1.setName(j.getName());
                        fileModel1.setExtension(extension);
                        fileModel1.setPath(j.getParent());
                        fileModel1.setDate(LocalDateTime.now());
                        float scale = (float) Math.pow(10, 5);
                        fileModel1.setSize((Math.round(((float) j.length() / (1024 * 1024)) * scale) / scale));
                        fileRepo.save(fileModel1);

                    });
                } else {
                    FileModel fileModel = new FileModel();

                    String extension = i.getName().substring(i.getName().indexOf(".") + 1);
                    fileModel.setName(i.getName());
                    fileModel.setExtension(extension);
                    fileModel.setPath(i.getParent());
                    fileModel.setDate(LocalDateTime.now());
                    float scale = (float) Math.pow(10, 5);
                    fileModel.setSize((Math.round(((float) i.length() / (1024 * 1024)) * scale) / scale));
                    fileRepo.save(fileModel);
                }
            });



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(HttpStatus.OK);
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


        if (file.delete()) {

            fileRepo.deleteByNameAndPath(name, path);
            return new ResponseEntity<>(HttpStatus.OK);

        }
        else {
            throw new DeleteFileException();
        }


    }



    @Transactional
    public ResponseEntity<?> deleteDirectory(String name, String path) {


        File file = new File(path + "/" + name);


        delete.deleteDirectory(file);

        return new ResponseEntity<>("Файл успешно удален", HttpStatus.OK);

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

    public ResponseEntity<?> downloadDirectory(String path, String name) {

        try {
            Path zipFilePath = Files.createTempFile("archive", ".zip");
            try
                    (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile())))
            {

                Path sourceDir = Paths.get(path + "/" + name);



                Files.walk(sourceDir).filter(j -> Files.isRegularFile(j)).forEach(i -> {

                    ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(i).toString());

                    try {
                        zout.putNextEntry(zipEntry);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try (FileInputStream fis = new FileInputStream(i.toFile())) {
                        byte[] buffer = new byte[fis.available()];

                        fis.read(buffer);
                        zout.write(buffer);
                        zout.closeEntry();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String encodedName = URLEncoder.encode(name + ".zip", StandardCharsets.UTF_8.toString());
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=\"" + name + ".zip\"; filename*=UTF-8''" + encodedName)
                    .contentType(MediaType.valueOf("application/zip"))
                    .body(Files.readAllBytes(zipFilePath));





        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
