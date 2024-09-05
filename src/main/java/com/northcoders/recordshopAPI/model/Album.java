package com.northcoders.recordshopAPI.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue
    private long id;
    @Column
    private String title;
    @Column
    private String artist;
    @Column
    private Integer releasedYear;
    @Column
    private Genre genre;

}