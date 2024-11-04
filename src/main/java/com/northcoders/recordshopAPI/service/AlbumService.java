package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AlbumService {


    List<AlbumModel> getAllAlbums();

    List<AlbumModel> getAllAlbumsInStock();

    Mono<AlbumModel> saveAlbum(AlbumModel album);

    AlbumModel getAlbumById(Long id);

    String deleteAlbumById(Long id);

    List<AlbumModel> getAllAlbumsByArtist(String Artist);

    List<AlbumModel> getAllAlbumsByReleasedYear(Integer year);

    AlbumModel getAlbumByTitle(String title);

    Mono<AlbumModel> updateAlbum(Long id, AlbumModel album);

    AlbumModel partiallyUpdateAlbum(Long id, AlbumModel album);

    List<AlbumModel> getAllAlbumsByGenre(String genre);
}

