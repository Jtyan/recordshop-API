package com.northcoders.recordshopAPI.repository;

import com.northcoders.recordshopAPI.model.Album;
import org.springframework.data.repository.CrudRepository;

public interface AlbumRepository extends CrudRepository<Album, Long> {
}
