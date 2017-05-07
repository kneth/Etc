package net.zigzak.etc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root(name = "text", strict = false)
public class Text {
    @Attribute(name = "prev", required = false)
    private String prev;

    @Attribute(name = "next", required = false)
    private String next;

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
