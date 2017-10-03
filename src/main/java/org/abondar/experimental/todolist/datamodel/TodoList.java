package org.abondar.experimental.todolist.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ApiModel(value="List", description="TodoList ")
public class TodoList {

    @ApiModelProperty(value = "ID of list")
    private Long id;

    @ApiModelProperty(value = "Name of list")
    private String name;

    @ApiModelProperty(value = "ID of user whose list it is")
    private Long userId;

    public TodoList(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    public TodoList(){}

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                '}';
    }
}
