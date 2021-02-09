package br.edu.ifrs.musicnotes.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Album implements Serializable {

    private String id;
    private String title;
    private List<String> artists;
    private List<String> tags;
    private Map<String, String> images;
    private int year;
    private String review;
    private float rating;
    private long updatedAt;

    public Album(String id, String title, List<String> artists, Map<String, String> images, int year) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.images = images;
        this.year = year;
    }
}
