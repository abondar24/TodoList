package org.abondar.experimental.todolist.datamodel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Item {

    private Long id;
    private String name;
    private Boolean done;
    private Long listId;

    public Item(String name, Boolean done, Long listId) {
        this.name = name;
        this.done = done;
        this.listId = listId;
    }

    public Item(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", done=" + done +
                ", listId=" + listId +
                '}';
    }
}
