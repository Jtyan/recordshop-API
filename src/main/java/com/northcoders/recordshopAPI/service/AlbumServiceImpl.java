package com.northcoders.recordshopAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        try {
            albumRepository.findAllByOrderByIdDesc().forEach(albumList::add);
        }catch (RuntimeException e){
            throw new RuntimeException("Failed to retrieve albums list", e);
        }
        return albumList;
    }

    @Override
    public List<AlbumModel> getAllAlbumsInStock() {
        List<AlbumModel> albumList = new ArrayList<>();
        albumRepository.findAllByOrderByIdDesc().forEach(album -> {
            if (album.getStock() != null && album.getStock() > 0) {
                albumList.add(album);
            }
        });
        return albumList;
    }

    @Override
    public Mono<AlbumModel> saveAlbum(AlbumModel album) {
        return fetchAlbumCoverURL(album.getTitle(), album.getArtist())
                .defaultIfEmpty("") // Optionally provide a default URL if not found
                .flatMap(albumCoverURL -> {
                    album.setAlbumCoverURL(albumCoverURL);
                    return Mono.fromCallable(() -> albumRepository.save(album))
                            .doOnSuccess(savedAlbum -> {
                                log.info("{} successfully added", savedAlbum.getTitle());
                                cacheService.evictCacheValue("allAlbums", "all");
                            });
                })
                .onErrorMap(DataIntegrityViolationException.class,
                        ex -> new AlbumAlreadyExistsException("Album already exists!"));
    }

    @Override
    public AlbumModel getAlbumById(Long id) {
        log.info("Fetching album with id: {}", id);
        return cacheService.getCachedValue("albums", id, TimeUnit.SECONDS.toMillis(30),
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
        return cacheService.getCachedValue("albumsByArtist", artist, TimeUnit.SECONDS.toMillis(30),
                () -> albumRepository.findByArtist(artist));
    }

    @Override
    @Cacheable("albumsByYear")
    public List<AlbumModel> getAllAlbumsByReleasedYear(Integer year) {
        log.info("Fetching albums released in year: {}", year);
        return cacheService.getCachedValue("albumsByYear", year, TimeUnit.SECONDS.toMillis(30),
                () -> albumRepository.findByReleasedYear(year));
    }

    @Override
    public AlbumModel getAlbumByTitle(String title) {
        log.info("Fetching album by title: {}", title);
        return cacheService.getCachedValue("albumsByTitle", title, TimeUnit.SECONDS.toMillis(30),
                () -> albumRepository.findByTitle(title));
    }

    @Override
    public List<AlbumModel> getAllAlbumsByGenre(String genre) {
        log.info("Fetching album by genre: {}", genre);
        return cacheService.getCachedValue("albumsByGenre", genre, TimeUnit.SECONDS.toMillis(30),
                () -> albumRepository.findByGenre(Genre.valueOf(genre)));
    }

    @Override
    public Mono<AlbumModel> updateAlbum(Long id, AlbumModel album) {
        String title = album.getTitle();
        String artist = album.getArtist();

        if (title == null || artist == null) {
            throw new NullPointerException("Title and artist cannot be null");
        } else if (albumRepository.findById(id).isEmpty()) {
            throw new AlbumNotFoundException("Album with ID = " + id + " not found");
        }
        return fetchAlbumCoverURL(title, artist)
                .defaultIfEmpty("") // Optionally provide a default URL if not found
                .flatMap(albumCoverURL -> {
                    album.setId(id);
                    album.setAlbumCoverURL(albumCoverURL);
                    return Mono.fromCallable(() -> albumRepository.save(album))
                            .doOnSuccess(savedAlbum -> {
                                log.info("{} successfully updated ", savedAlbum.getTitle());
                                cacheService.evictCacheValue("album", id);
                            });
                })
                .onErrorMap(DataIntegrityViolationException.class,
                        ex -> new AlbumAlreadyExistsException("Album already exists!"));
    }

    @Override
    @CachePut("albums")
    public Mono<AlbumModel> partiallyUpdateAlbum(Long id, AlbumModel album) {
        Optional<AlbumModel> existingAlbumOpt = albumRepository.findById(id);
        String title = album.getTitle();
        String artist = album.getArtist();

        return fetchAlbumCoverURL(title, artist)
                .defaultIfEmpty("")
                .flatMap(albumCoverURL -> {
                    if (existingAlbumOpt.isPresent()) {
                        AlbumModel existingAlbum = existingAlbumOpt.get();
                        if (album.getTitle() != null) {
                            existingAlbum.setTitle(album.getTitle());
                        }
                        if (album.getArtist() != null) {
                            existingAlbum.setArtist(album.getArtist());
                        }
                        if (album.getReleasedYear() != null) {
                            existingAlbum.setReleasedYear(album.getReleasedYear());
                        }
                        if (album.getGenre() != null) {
                            existingAlbum.setGenre(album.getGenre());
                        }
                        if (album.getStock() != null) {
                            existingAlbum.setStock(album.getStock());
                        }
                        existingAlbum.setAlbumCoverURL(albumCoverURL);
                        return Mono.fromCallable(() -> albumRepository.save(existingAlbum))
                                .doOnSuccess(savedAlbum -> {
                                    log.info("{} successfully updated ", savedAlbum.getTitle());
                                    cacheService.evictCacheValue("album", id);
                                })
                                .onErrorMap(DataIntegrityViolationException.class,
                                        ex -> new AlbumAlreadyExistsException("Album already exists!"));
                    } else {
                        throw new AlbumNotFoundException("Album with ID " + id + " not found");
                    }
                });
    }

    private Mono<String> fetchAlbumCoverURL(String title, String artist) {
        String url = "https://itunes.apple.com/search?term=" + title + "+" + artist + "&entity=album";
        WebClient webClient = WebClient.create();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jsonResponse -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(jsonResponse);
                        JsonNode resultsNode = rootNode.path("results");

                        if (resultsNode.isArray() && resultsNode.size() > 0) {
                            JsonNode firstResult = resultsNode.get(0); // Take the first result
                            String artworkUrl = firstResult.path("artworkUrl100").asText(); // or "artworkUrl600" for higher resolution
                            return Mono.just(artworkUrl);
                        } else {
                            return Mono.empty(); // No results found
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse album cover URL from iTunes API response", e);
                        return Mono.empty();
                    }
                });

    }
}
