package com.fedorov.storage.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity(name = "files")
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "extension")
    private String extension;

    @Column(name = "path")
    private String path;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "size")
    private int size;


    public FileModel(int id, String name, String extension, String path, LocalDateTime date, int size) {
        this.id = id;
        this.name = name;
        this.extension = extension;
        this.path = path;
        this.date = date;
        this.size = size;
    }

    public FileModel() {
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getPath() {
        return this.path;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public int getSize() {
        return this.size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
