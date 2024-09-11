package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.exception.AlbumAlreadyExistsException;
import com.northcoders.recordshopAPI.exception.AlbumNotFoundException;
import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import com.northcoders.recordshopAPI.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private final AlbumRepository albumRepository;
    private final GenericCacheService cacheService;

    public AlbumServiceImpl(AlbumRepository albumRepository, GenericCacheService cacheService) {
        this.albumRepository = albumRepository;
        this.cacheService = cacheService;
    }

    @Override
    public List<AlbumModel> getAllAlbums() {
        List<AlbumModel> albumList = new ArrayList<>();
        albumRepository.findAllByOrderByIdAsc().forEach(albumList::add);
        return albumList;
    }

    @Override
    public List<AlbumModel> getAllAlbumsInStock() {
        List<AlbumModel> albumList = new ArrayList<>();
        albumRepository.findAllByOrderByIdAsc().forEach(album -> {
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

            cacheService.evictCacheValue("allAlbums", "all");
            return savedAlbum;
        } catch (DataIntegrityViolationException e) {
            throw new AlbumAlreadyExistsException("Album already exists!");
        }
    }

    @Override
    public AlbumModel getAlbumById(Long id) {
        log.info("Fetching album with id: {}", id);
        return cacheService.getCachedValue("albums", id, TimeUnit.SECONDS.toMillis(60),
                () -> {
                    System.out.println("Fetching from database...");
                    return albumRepository.findById(id)
                            .orElseThrow(() -> new AlbumNotFoundException("Album with ID = " + id + " not found"));
                });
    }

    @Override
    public String deleteAlbumById(Long id) {
        log.info("Deleting album with ID = {}", id);
        albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException("Album with ID = " + id + " not found"));
        albumRepository.deleteById(id);
        cacheService.evictCacheValue("albums", id);
        return "Album successfully deleted";
    }

    @Override
    public List<AlbumModel> getAllAlbumsByArtist(String artist) {
        log.info("Fetching albums by artist: {}", artist);
        return cacheService.getCachedValue("albumsByArtist", artist, TimeUnit.SECONDS.toMillis(60),
                () -> albumRepository.findByArtist(artist));
    }

    @Override
    @Cacheable("albumsByYear")
    public List<AlbumModel> getAllAlbumsByReleasedYear(Integer year) {
        log.info("Fetching albums released in year: {}", year);
        return cacheService.getCachedValue("albumsByYear", year, TimeUnit.SECONDS.toMillis(60),
                () -> albumRepository.findByReleasedYear(year));
    }

    @Override
    public AlbumModel getAlbumByTitle(String title) {
        log.info("Fetching album by title: {}", title);
        return cacheService.getCachedValue("albumsByTitle", title, TimeUnit.SECONDS.toMillis(60),
                () -> albumRepository.findByTitle(title));
    }

    @Override
    public List<AlbumModel> getAllAlbumsByGenre(String genre) {
        log.info("Fetching album by genre: {}", genre);
        return cacheService.getCachedValue("albumsByGenre", genre, TimeUnit.SECONDS.toMillis(60),
                () -> albumRepository.findByGenre(Genre.valueOf(genre)));
    }

    @Override
    public AlbumModel updateAlbum(Long id, AlbumModel album) {
        album.setId(id);
        AlbumModel updatedAlbum = albumRepository.save(album);
        cacheService.evictCacheValue("album", id);
        return updatedAlbum;
    }

    @Override
    @CachePut("albums")
    public AlbumModel partiallyUpdateAlbum(Long id, AlbumModel newAlbum) {
        Optional<AlbumModel> existingAlbumOpt = albumRepository.findById(id);

        if (existingAlbumOpt.isPresent()) {
            AlbumModel existingAlbum = existingAlbumOpt.get();

            if (newAlbum.getTitle() != null) {
                existingAlbum.setTitle(newAlbum.getTitle());
            }
            if (newAlbum.getArtist() != null) {
                existingAlbum.setArtist(newAlbum.getArtist());
            }
            if (newAlbum.getReleasedYear() != null) {
                existingAlbum.setReleasedYear(newAlbum.getReleasedYear());
            }
            if (newAlbum.getGenre() != null) {
                existingAlbum.setGenre(newAlbum.getGenre());
            }
            if (newAlbum.getStock() != null) {
                existingAlbum.setStock(newAlbum.getStock());
            }

            AlbumModel updatedAlbum = albumRepository.save(existingAlbum);
            cacheService.evictCacheValue("albums", id);
            return updatedAlbum;
        } else {
            throw new AlbumNotFoundException("Album with ID " + id + " not found");
        }
    }

}
