package de.neofonie.surlgen.processor.spring;

public class HelloWorldCommand {

    private int id;
    private String caption;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "HelloWorldCommand{" +
                "id=" + id +
                ", caption='" + caption + '\'' +
                '}';
    }
}
