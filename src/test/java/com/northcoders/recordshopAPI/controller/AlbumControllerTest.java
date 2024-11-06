package com.northcoders.recordshopAPI.controller;

import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import com.northcoders.recordshopAPI.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService mockAlbumService;

    private List<AlbumModel> mockAlbumList;

    private final String URI = "/api/v1";


    @BeforeEach
    void setUp() {
        mockAlbumList = List.of(
                new AlbumModel(1L, "Abbey Road", "The Beatles", 1969, Genre.ROCK, 5, ""),
                new AlbumModel(2L, "Thriller", "Michael Jackson", 1982, Genre.POP, 77, ""),
                new AlbumModel(3L, "Kind of Blue", "Miles Davis", 1959, Genre.JAZZ, 10, ""),
                new AlbumModel(4L, "The Planets", "Gustav Holst", 1918, Genre.CLASSICAL, 0, ""),
                new AlbumModel(5L, "Blue", "Joni Mitchell", 1971, Genre.FOLK, 100, ""),
                new AlbumModel(6L, "Symphony No. 9", "Ludwig van Beethoven", 1824, Genre.CLASSICAL, 45, ""),
                new AlbumModel(7L, "Back in Black", "AC/DC", 1980, Genre.ROCK, 0, ""),
                new AlbumModel(8L, "The Freewheelin' Bob Dylan", "Bob Dylan", 1963, Genre.FOLK, null, "")
        );
    }

    @Test
    void getAllAlbums() throws Exception {
        when(mockAlbumService.getAllAlbums()).thenReturn(mockAlbumList);

        mockMvc.perform(get(URI + "/album").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Abbey Road"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releasedYear").value(1969));
    }

    @Test
    void getAllAlbums_notFound() throws Exception {
        when(mockAlbumService.getAllAlbums()).thenThrow(new RuntimeException("Failed to retrieve albums list"));

        mockMvc.perform(get(URI + "/album").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(result -> assertInstanceOf(RuntimeException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Failed to retrieve albums list",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void getAllAlbumsInStock() throws Exception {
        List<AlbumModel> albumListInStock = new ArrayList<>();
        for (AlbumModel album : mockAlbumList) {
            if (album.getStock() != null && album.getStock() > 0) {
                albumListInStock.add(album);
            }
        }
        when(mockAlbumService.getAllAlbumsInStock()).thenReturn(albumListInStock);

        mockMvc.perform(get(URI + "/album?inStock=true").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].id").value(5L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].title").value("Blue"));
    }

    @Test
    void getAlbumById() throws Exception {
        when(mockAlbumService.getAlbumById(2L)).thenReturn(mockAlbumList.get(1));

        mockMvc.perform(get(URI + "/album/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Thriller"));
    }

    @Test
    void getAlbumsByArtist() throws Exception {
        List<AlbumModel> albumListByArtist = new ArrayList<>();
        for (AlbumModel album : mockAlbumList) {
            if (album.getArtist() == "The Beatles") {
                albumListByArtist.add(album);
            }
        }
        when(mockAlbumService.getAllAlbumsByArtist("The Beatles")).thenReturn(albumListByArtist);

        mockMvc.perform(get(URI + "/album?artist=The Beatles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].artist").value("The Beatles"));
    }

    @Test
    void getAlbumsByReleasedYear() throws Exception {
        List<AlbumModel> albumListByReleasedYear = new ArrayList<>();
        for (AlbumModel album : mockAlbumList) {
            if (album.getReleasedYear() == 1969) {
                albumListByReleasedYear.add(album);
            }
        }
        when(mockAlbumService.getAllAlbumsByReleasedYear(1969)).thenReturn(albumListByReleasedYear);
        mockMvc.perform(get(URI + "/album?releasedYear=1969").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releasedYear").value(1969));
    }

    @Test
    void getAlbumByTitle() throws Exception {
        when(mockAlbumService.getAlbumByTitle("Abbey Road")).thenReturn(mockAlbumList.getFirst());

        mockMvc.perform(get(URI + "/album/title/Abbey Road").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Abbey Road"));
    }

    @Test
    void getAlbumsByGenre() throws Exception {
        List<AlbumModel> AlbumListByGenre = new ArrayList<>();
        for (AlbumModel album : mockAlbumList) {
            if (album.getGenre() == Genre.ROCK) {
                AlbumListByGenre.add(album);
            }
        }
        when(mockAlbumService.getAllAlbumsByGenre("ROCK")).thenReturn(AlbumListByGenre);
        mockMvc.perform(get(URI + "/album?genre=ROCK").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].genre").value("ROCK"));
    }

    @Test
    void addAlbum() throws Exception {
        AlbumModel album = new AlbumModel(20L, "21", "Adele", 2011, Genre.POP, 20, "");
        given(mockAlbumService.saveAlbum(any(AlbumModel.class))).willReturn(Mono.just(album));

        MvcResult result = mockMvc.perform(post(URI + "/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "21",
                                    "artist": "Adele",
                                    "releasedYear": 2011,
                                    "genre": "POP",
                                    "stock": 20
                                }"""))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("21"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.artist").value("Adele"));
    }

    @Test
    void updateAlbum() throws Exception {
        AlbumModel newAlbum = new AlbumModel(1L, "21", "Adele", 2011, Genre.POP, 20, "");

        given(mockAlbumService.updateAlbum(any(Long.class), any(AlbumModel.class))).willReturn(Mono.just(newAlbum));

        MvcResult result = mockMvc.perform(put(URI + "/album/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "21",
                                    "artist": "Adele",
                                    "releasedYear": 2011,
                                    "genre": "POP",
                                    "stock": 20
                                }"""))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("21"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.artist").value("Adele"));
    }

    @Test
    void partiallyUpdateAlbum() throws Exception {
        AlbumModel newAlbum = new AlbumModel(1L, "21", "Adele", 2011, Genre.POP, 20, "");

        given(mockAlbumService.updateAlbum(any(Long.class), any(AlbumModel.class))).willReturn(Mono.just(newAlbum));

        MvcResult result = mockMvc.perform(put(URI + "/album/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "21",
                                    "artist": "Adele",
                                    "releasedYear": 2011,
                                    "genre": "POP",
                                    "stock": 20
                                }"""))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("21"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.artist").value("Adele"));
    }

    @Test
    void deleteAlbumById() throws Exception {
        when(mockAlbumService.deleteAlbumById(2L)).thenReturn("Album successfully deleted");

        mockMvc.perform(delete(URI + "/album/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().string("Album successfully deleted"));
    }
}