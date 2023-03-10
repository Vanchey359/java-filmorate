package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Size(min = 1, max = 200)
    private String description;

    @NotNull
    @Past
    private LocalDate releaseDate;

    @Positive
    @NotNull
    private int duration;

    private Set<Genre> genres;
    @NotNull private Mpa mpa;

    private Long rateId;

    private final Set<Long> likes = new TreeSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration,
                Set<Genre> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, Set<Genre> genres,
                Mpa mpa, Long rateId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
        this.rateId = rateId;
    }

    public int getRating(){
        return likes.size();
    }

    public boolean hasLikeFromUser(Long userId) {
        return likes.contains(userId);
    }

    public void addLikeFromUser(Long userId) {
        likes.add(userId);
    }

    public void removeLikeFromUser(Long userId) {
        likes.remove(userId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("releaseDate", releaseDate);
        values.put("duration", duration);
        return values;
    }
}
