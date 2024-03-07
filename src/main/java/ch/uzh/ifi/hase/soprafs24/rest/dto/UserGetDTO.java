package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserGetDTO {

  private Long userid;
  private String name;
  private String username;
  private String creationdate;
  private UserStatus status;

  private String birthday;

  private String token;

  public Long getId() {
    return userid;
  }

  public void setId(Long id) {
    this.userid = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getCreationdate() {
    return creationdate;
}

  public void setCreationdate(String creationdate) {
    this.creationdate = creationdate;
}

  public String getBirthday() {
    return birthday;
}

  public void setBirthday(String birthday) {
    this.birthday = birthday;
}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
