package pl.edu.pb.libraryappwithapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("author_name")
    private List<String> authors;
    @SerializedName("cover_i")
    private String cover;
    @SerializedName("number_of_pages_median")
    private String numberOfPages;
    @SerializedName("first_publish_year")
    private int firstPublishYear;
    @SerializedName("language")
    private List<String> languages;
    @SerializedName("publisher")
    private List<String> publishers;

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getCover() {
        return cover;
    }

    public String getNumberOfPages() {
        return numberOfPages;
    }

    public int getFirstPublishYear() {
        return firstPublishYear;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setNumberOfPages(String numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setFirstPublishYear(int firstPublishYear) {this.firstPublishYear = firstPublishYear;}

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }
}
