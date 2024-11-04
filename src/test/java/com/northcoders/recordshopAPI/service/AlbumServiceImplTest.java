package com.northcoders.recordshopAPI.service;

import com.northcoders.recordshopAPI.exception.AlbumAlreadyExistsException;
import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import com.northcoders.recordshopAPI.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository mockAlbumRepository;

    @Mock
    private GenericCacheService mockCacheService;

    @InjectMocks
    private AlbumServiceImpl albumService;

    List<AlbumModel> mockAlbumList= new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockAlbumList.add(new AlbumModel(1L, "Abbey Road", "The Beatles", 1969, Genre.ROCK, 5,""));
        mockAlbumList.add(new AlbumModel(2L, "Thriller", "Michael Jackson", 1982, Genre.POP, 77,""));
        mockAlbumList.add(new AlbumModel(3L, "Kind of Blue", "Miles Davis", 1959, Genre.JAZZ, 10,""));
        mockAlbumList.add(new AlbumModel(4L, "The Planets", "Gustav Holst", 1918, Genre.CLASSICAL, 0,""));
        mockAlbumList.add(new AlbumModel(5L, "Blue", "Joni Mitchell", 1971, Genre.FOLK, 100,""));
        mockAlbumList.add(new AlbumModel(6L, "Symphony No. 9", "Ludwig van Beethoven", 1824, Genre.CLASSICAL, 45,""));
        mockAlbumList.add(new AlbumModel(7L, "Back in Black", "AC/DC", 1980, Genre.ROCK, 0,""));
        mockAlbumList.add(new AlbumModel(8L, "The Freewheelin' Bob Dylan", "Bob Dylan", 1963, Genre.FOLK, null,""));
    }

    @Test
    @DisplayName("return correct number of albums when given list of albums")
    void testGetAllAlbums() {

        when(mockAlbumRepository.findAllByOrderByIdDesc()).thenReturn(mockAlbumList);

        List<AlbumModel> albums = albumService.getAllAlbums();
        assertEquals(8, albums.size());
    }

    @Test
    @DisplayName("return correct number of albums in stock when given list of albums")
    void testGetAllAlbumsInStock() {

        when(mockAlbumRepository.findAllByOrderByIdDesc()).thenReturn(mockAlbumList);

        List<AlbumModel> albums = albumService.getAllAlbumsInStock();
        assertEquals(5, albums.size());
    }

    @Test
    @DisplayName("return correct album when given album to save")
    void testSaveAlbumWithValidAlbum() {
        AlbumModel mockAlbum = new AlbumModel(10L, "1989", "Taylor Swift", 2014, Genre.POP, 22, "");

        when(mockAlbumRepository.save(mockAlbum)).thenReturn(mockAlbum);

        Mono<AlbumModel> savedAlbumMono = albumService.saveAlbum(mockAlbum);

        StepVerifier.create(savedAlbumMono)
                .assertNext(savedAlbum -> {
                    assertEquals("1989", savedAlbum.getTitle());
                    assertEquals("Taylor Swift", savedAlbum.getArtist());
                    assertEquals(Genre.POP, savedAlbum.getGenre());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("throw AlbumAlreadyExistsException when given a duplicated album to save")
    void testSaveAlbumWithDuplicatedAlbum() {
        AlbumModel mockAlbum = new AlbumModel(10L, "1989", "Taylor Swift", 2014, Genre.POP, 22, "");

        when(mockAlbumRepository.save(mockAlbum)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlbumAlreadyExistsException.class, () -> albumService.saveAlbum(mockAlbum));
    }

}