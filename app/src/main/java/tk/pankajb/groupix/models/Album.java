package tk.pankajb.groupix.models;

public class Album {

    private String name;
    private String description;
    private String coverImage;

    public Album() {
        // Required for database
    }

    public Album(String name, String description, String coverImage) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImage() {
        return coverImage;
    }

}
