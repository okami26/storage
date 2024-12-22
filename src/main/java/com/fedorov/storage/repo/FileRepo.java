package com.fedorov.storage.repo;

import com.fedorov.storage.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileModel, Integer> {

    Optional<FileModel> findByNameAndPath(String name, String path);

    void deleteByNameAndPath(String name, String path);
}
