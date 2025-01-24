package com.fedorov.storage.utils;

import com.fedorov.storage.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Component
public class Delete {

    @Autowired
    private FileRepo fileRepo;
    @Transactional
    public void deleteDirectory(File file) {

        String path = file.getPath().replace("\\", "/").substring(0, file.getPath().replace("\\", "/").lastIndexOf("/"));
        String name = file.getName();
        System.out.println(path);
        System.out.println(name);

        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {


            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {

                    deleteDirectory(f);
                }
            }
        }

        file.delete();
        fileRepo.deleteByNameAndPath(name, path);



    }



}
