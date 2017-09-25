package org.abondar.experimental.todolist.datamodel;

public class TodoList {

    private Long id;
    private String name;
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
