package com.fedorov.storage.repo;

import com.fedorov.storage.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileModel, Integer> {

    Optional<FileModel> findByNameAndPath(String name, String path);

    Optional<List<FileModel>> findAllByPath(String path);

    Optional<List<FileModel>> findAllByPathOrderByNameAsc(String path);

    Optional<List<FileModel>> findAllByPathOrderByNameDesc(String path);

    Optional<List<FileModel>> findAllByPathOrderByExtensionAsc(String path);

    Optional<List<FileModel>> findAllByPathOrderByExtensionDesc(String path);


    Optional<List<FileModel>> findAllByPathOrderByDateDesc(String path);

    Optional<List<FileModel>> findAllByPathOrderByDateAsc(String path);

    Optional<List<FileModel>> findAllByPathOrderBySizeAsc(String path);

    Optional<List<FileModel>> findAllByPathOrderBySizeDesc(String path);




    void deleteByNameAndPath(String name, String path);


}
