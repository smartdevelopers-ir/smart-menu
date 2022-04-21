package ir.smartdevelopers.smartpopupmenu;

public class MenuItem {
    private int id;
    private String title;
    private int mIconRes;

    public MenuItem(int id, String title, int iconRes) {
        this.id = id;
        this.title = title;
        mIconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public void setIconRes(int iconRes) {
        mIconRes = iconRes;
    }
}
