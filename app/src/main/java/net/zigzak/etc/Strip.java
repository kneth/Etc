package net.zigzak.etc;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Strip extends RealmObject {
    @PrimaryKey
    private String id;

    private String prev;
    private String next;

    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
