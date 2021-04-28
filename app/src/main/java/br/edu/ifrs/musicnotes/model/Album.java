package br.edu.ifrs.musicnotes.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Album implements Serializable {

    @Exclude
    private String id;
    @Exclude
    private String title;
    @Exclude
    private List<String> artists;
    @Exclude
    private Map<String, String> images;
    @Exclude
    private int year;
    private List<String> tags;
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

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getTitle() {
        return title;
    }

    @Exclude
    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public List<String> getArtists() {
        return artists;
    }

    @Exclude
    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    @Exclude
    public Map<String, String> getImages() {
        return images;
    }

    @Exclude
    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    @Exclude
    public int getYear() {
        return year;
    }

    @Exclude
    public void setYear(int year) {
        this.year = year;
    }
}
