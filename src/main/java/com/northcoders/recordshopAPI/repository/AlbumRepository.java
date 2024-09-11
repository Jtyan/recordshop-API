package com.northcoders.recordshopAPI.repository;

import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends CrudRepository<AlbumModel, Long> {
    List<AlbumModel> findAllByOrderByIdAsc();
    AlbumModel findByTitle(String title);
    List<AlbumModel> findByArtist(String artist);
    List<AlbumModel> findByGenre(Genre genre);
    List<AlbumModel> findByReleasedYear(Integer year);

}
