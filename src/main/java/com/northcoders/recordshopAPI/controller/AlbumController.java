package com.northcoders.recordshopAPI.controller;

import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AlbumController {

    @Autowired
    AlbumService albumService;

    @GetMapping("/album")
    public ResponseEntity<List<AlbumModel>> getAllAlbums() {
        List<AlbumModel> albums = albumService.getAllAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping(value = "/album", params = "inStock=true")
    public ResponseEntity<List<AlbumModel>> getAllAlbumsInStock(@RequestParam("inStock") Boolean inStock) {
        List<AlbumModel> albums = albumService.getAllAlbumsInStock();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<AlbumModel> getAlbumById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(albumService.getAlbumById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/album", params = "artist")
    public ResponseEntity<List<AlbumModel>> getAlbumsByArtist(@RequestParam String artist) {
        List<AlbumModel> albums = albumService.getAllAlbumsByArtist(artist);
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping(value = "/album", params = "releasedYear")
    public ResponseEntity<List<AlbumModel>> getAlbumsByReleasedYear(@RequestParam("releasedYear") Integer year) {
        List<AlbumModel> albums = albumService.getAllAlbumsByReleasedYear(year);
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping(value = "/album/title/{title}")
    public ResponseEntity<AlbumModel> getAlbumByTitle(@PathVariable("title") String title) {
        AlbumModel album = albumService.getAlbumByTitle(title);
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @GetMapping(value = "/album", params = "genre")
    public ResponseEntity<List<AlbumModel>> getAlbumsByGenre(@RequestParam("genre") String genre) {
        List<AlbumModel> albums = albumService.getAllAlbumsByGenre(genre);
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @PostMapping("/album")
    public ResponseEntity<AlbumModel> addAlbum(@RequestBody AlbumModel album) {
        AlbumModel newAlbum = albumService.saveAlbum(album);
        return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    @PutMapping("/album/{id}")
    public ResponseEntity<AlbumModel> updateAlbum(@PathVariable("id") Long id, @RequestBody AlbumModel album) {
        AlbumModel updatedAlbum = albumService.updateAlbum(id, album);
        return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
    }

    @PatchMapping("/album/{id}")
    public ResponseEntity<AlbumModel> partiallyUpdateAlbum(@PathVariable("id") Long id, @RequestBody AlbumModel album) {
        AlbumModel updatedAlbum = albumService.partiallyUpdateAlbum(id, album);
        return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
    }

    @DeleteMapping("/album/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable("id") Long id) {
        return new ResponseEntity<>(albumService.deleteAlbumById(id), HttpStatus.ACCEPTED);
    }
}
