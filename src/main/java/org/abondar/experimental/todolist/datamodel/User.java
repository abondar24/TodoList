package org.abondar.experimental.todolist.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ApiModel(value="User", description="User data")
public class User {

    @ApiModelProperty(value = "ID of user")
    private Long id;

    @ApiModelProperty(value = "Username")
    private String username;

    @ApiModelProperty(value = "User's password")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
