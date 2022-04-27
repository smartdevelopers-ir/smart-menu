package ir.smartdevelopers.smartpopupmenu;

import java.util.Objects;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return id == menuItem.id && Objects.equals(title, menuItem.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
