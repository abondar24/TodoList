package org.abondar.experimental.todolist.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ApiModel(value="Item", description="Item of list")
public class Item {

    @ApiModelProperty(value = "ID of item")
    private Long id;

    @ApiModelProperty(value = "Name of item")
    private String name;

    @ApiModelProperty(value = "Is marked as done or not")
    private Boolean done;

    @ApiModelProperty(value = "ID of list in which the current item is")
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
