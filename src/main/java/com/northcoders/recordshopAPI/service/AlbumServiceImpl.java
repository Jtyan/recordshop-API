package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.model.Album;
import com.northcoders.recordshopAPI.repository.AlbumRepository;
import org.hibernate.type.descriptor.sql.internal.NativeEnumDdlTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    AlbumRepository albumRepository;

    @Override
    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<>();
        albumRepository.findAll().forEach(albumList::add);
        return albumList;
    }

    @Override
    public String saveAlbum(Album album) {
        try {
            albumRepository.save(album);
            return album.getTitle() + " successfully added";
        } catch (Exception e) {
            throw new NullPointerException("failed");
        }
    }

    @Override
    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public String deleteAlbumById(Long id) {
        try {
            albumRepository.deleteById(id);
            return "successfully deleted";
        } catch (Exception e) {
            throw new NullPointerException("failed");
        }
    }
}
