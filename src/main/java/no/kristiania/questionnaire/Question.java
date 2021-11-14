package no.kristiania.questionnaire;

public class Question {

    private long Id;
    private String Title;
    private String Text;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    @Override
    public String toString() {
        return "Question{" +
                "Id=" + Id +
                ", Title='" + Title + '\'' +
                ", Text='" + Text + '\'' +
                '}';
    }
}
