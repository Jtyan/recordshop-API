package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.exception.AlbumAlreadyExistsException;
import com.northcoders.recordshopAPI.exception.AlbumNotFoundException;
import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import com.northcoders.recordshopAPI.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    AlbumRepository albumRepository;

    @Override
    public List<AlbumModel> getAllAlbums() {
        List<AlbumModel> albumList = new ArrayList<>();
        albumRepository.findAll().forEach(albumList::add);
        return albumList;
    }

    @Override
    public List<AlbumModel> getAllAlbumsInStock() {
        List<AlbumModel> albumList = new ArrayList<>();
        albumRepository.findAll().forEach(album -> {
            if (album.getStock() != null && album.getStock() > 0) {
                albumList.add(album);
            }
        });
        return albumList;
    }

    @Override
    public AlbumModel saveAlbum(AlbumModel album) {
        try {
            AlbumModel savedAlbum = albumRepository.save(album);
            log.info("{} successfully added", savedAlbum.getTitle());
            return savedAlbum;
        } catch (DataIntegrityViolationException e) {
            throw new AlbumAlreadyExistsException("Album already exists!");
        }
    }

    @Override
    public AlbumModel getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with ID = " + id + " not found"));
    }

    @Override
    public String deleteAlbumById(Long id) {
        log.info("Deleting album with ID = {}", id);
        AlbumModel existingAlbum = albumRepository.findById(id)
                .orElse(null);
        if (existingAlbum != null) {
            albumRepository.deleteById(id);
            return "Album successfully deleted";
        } else {
            throw new AlbumNotFoundException("Album with ID = " + id + " not found");
        }
    }

    @Override
    public List<AlbumModel> getAllAlbumsByArtist(String artist) {
        return albumRepository.findByArtist(artist);
    }

    @Override
    public List<AlbumModel> getAllAlbumsByReleasedYear(Integer year) {
        log.info("Fetching albums released in year: {}", year);
        return albumRepository.findByReleasedYear(year);
    }

    @Override
    public AlbumModel getAlbumByTitle(String title) {
        log.info("Fetching album by title: {}", title);
        return albumRepository.findByTitle(title);
    }

    @Override
    public List<AlbumModel> getAllAlbumsByGenre(String genre) {
        log.info("Fetching album by genre: {}", genre);
        return albumRepository.findByGenre(Genre.valueOf(genre));
    }

    @Override
    public AlbumModel updateAlbum(Long id, AlbumModel album) {
        album.setId(id);
        return albumRepository.save(album);
    }

    @Override
    public AlbumModel partiallyUpdateAlbum(Long id, AlbumModel updatedAlbum) {
        Optional<AlbumModel> existingAlbumOpt = albumRepository.findById(id);

        if (existingAlbumOpt.isPresent()) {
            AlbumModel existingAlbum = existingAlbumOpt.get();

            if (updatedAlbum.getTitle() != null) {
                existingAlbum.setTitle(updatedAlbum.getTitle());
            }
            if (updatedAlbum.getArtist() != null) {
                existingAlbum.setArtist(updatedAlbum.getArtist());
            }
            if (updatedAlbum.getReleasedYear() != null) {
                existingAlbum.setReleasedYear(updatedAlbum.getReleasedYear());
            }
            if (updatedAlbum.getGenre() != null) {
                existingAlbum.setGenre(updatedAlbum.getGenre());
            }
            if (updatedAlbum.getStock() != null) {
                existingAlbum.setStock(updatedAlbum.getStock());
            }

            return albumRepository.save(existingAlbum);
        } else {
            throw new AlbumNotFoundException("Album with ID " + id + " not found");
        }
    }

}
