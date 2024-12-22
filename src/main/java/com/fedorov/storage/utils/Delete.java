package com.fedorov.storage.utils;

import java.io.File;

public class Delete {

    public boolean deleteDirectory(String path, String name) {

        File file = new File(path + "/" + name);
        if (file.isDirectory()) {

            File[] files = file.listFiles();

            for (File i : files) {

                deleteDirectory(i.getParent(), i.getName());
            }
        }

        return file.delete();


    }

    public boolean deleteFile(String path, String name) {

        File file = new File(path + "/" + name);


        return file.delete();
    }

}
