package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.model.Album;

import java.util.List;

public interface AlbumService {


    public List<Album> getAllAlbums();

    public String saveAlbum(Album album);

    public Album getAlbumById(Long id);

    public String deleteAlbumById(Long id);
}

