package com.fedorov.storage.repo;

import com.fedorov.storage.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileModel, Integer> {

    Optional<FileModel> findByName(String name);

    void deleteByNameAndPath(String name, String path);
}
