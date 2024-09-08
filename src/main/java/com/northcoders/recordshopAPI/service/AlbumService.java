package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.model.AlbumModel;

import java.util.List;

public interface AlbumService {


    public List<AlbumModel> getAllAlbums();

    public AlbumModel saveAlbum(AlbumModel album);

    public AlbumModel getAlbumById(Long id);

    public String deleteAlbumById(Long id);

    public List<AlbumModel> getAllAlbumsByArtist(String Artist);

    public List<AlbumModel> getAllAlbumsByReleasedYear(Integer year);

    public AlbumModel getAlbumByTitle(String title);

    public AlbumModel updateAlbum(Long id, AlbumModel album);

    public AlbumModel partiallyUpdateAlbum(Long id, AlbumModel album);
}

