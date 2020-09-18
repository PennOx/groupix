package tk.pankajb.groupix.Album;

public class AlbumsDataModel {

    String name;
    String coverimg;
    String description;

    public AlbumsDataModel(String name, String coverimg, String description) {

        this.name = name;
        this.coverimg = coverimg;
        this.description = description;
    }

    public AlbumsDataModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverimg() {
        return coverimg;
    }

    public String getDescription() {
        return description;
    }
}
