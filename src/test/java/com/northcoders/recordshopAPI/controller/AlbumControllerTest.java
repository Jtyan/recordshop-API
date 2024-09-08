package com.northcoders.recordshopAPI.controller;

import com.northcoders.recordshopAPI.model.AlbumModel;
import com.northcoders.recordshopAPI.model.Genre;
import com.northcoders.recordshopAPI.service.AlbumService;
import com.northcoders.recordshopAPI.service.AlbumServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
                new AlbumModel(1L, "Abbey Road", "The Beatles", 1969, Genre.ROCK, 5),
                new AlbumModel(2L, "Thriller", "Michael Jackson", 1982, Genre.POP, 77),
                new AlbumModel(3L, "Kind of Blue", "Miles Davis", 1959, Genre.JAZZ, 10),
                new AlbumModel(4L, "The Planets", "Gustav Holst", 1918, Genre.CLASSICAL, 0),
                new AlbumModel(5L, "Blue", "Joni Mitchell", 1971, Genre.FOLK, 100),
                new AlbumModel(6L, "Symphony No. 9", "Ludwig van Beethoven", 1824, Genre.CLASSICAL, 45),
                new AlbumModel(7L, "Back in Black", "AC/DC", 1980, Genre.ROCK, 0),
                new AlbumModel(8L, "The Freewheelin' Bob Dylan", "Bob Dylan", 1963, Genre.FOLK, null)
        );
    }

    @Test
    void getAllAlbums() throws Exception{
        when(mockAlbumService.getAllAlbums()).thenReturn(mockAlbumList);
        when(mockAlbumService.getAllAlbumsInStock()).thenReturn(mockAlbumList);

        mockMvc.perform(get(URI + "/album").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Abbey Road"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].releasedYear").value(1969));
    }
}