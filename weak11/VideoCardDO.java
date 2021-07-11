

public class VideoCardDO {

    private String title;

    private int id;


    private int weight;

    List<VideoDO> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<VideoDO> getList() {
        return list;
    }

    public void setList(List<VideoDO> list) {
        this.list = list;
    }
}
