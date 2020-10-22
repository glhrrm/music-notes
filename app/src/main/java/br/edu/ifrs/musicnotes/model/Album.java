package br.edu.ifrs.musicnotes.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Album implements Serializable {

    private String id;
    private String title;
    private List<String> artists;
    private String image;
    private int year;
    private String review;
    private int rating;

    public Album(String id, String title, List<String> artists, String image, int year) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.image = image;
        this.year = year;
    }
}
