package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUser(Long id) {

    checkIfUserExistsProfile(id);
    return this.userRepository.findByUserid(id);}

  public User createUser(User newUser) {
      newUser.setToken(UUID.randomUUID().toString());
      newUser.setStatus(UserStatus.ONLINE);
      newUser.setCreationdate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
      checkIfUserExists(newUser);
      // saves the given entity but data is only persisted in the database once
      // flush() is called
      newUser = userRepository.save(newUser);
      userRepository.flush();

      log.debug("Created Information for User: {}", newUser);
      return newUser;
  }

  public void changeUser(User usertobechanged, long id) {

    checkIfUserExistsProfile(id);
    checkIfUserExistsEdit(usertobechanged, id);
    User user = this.userRepository.findByUserid(id);

    user.setUsername(usertobechanged.getUsername());

    user.setBirthday(usertobechanged.getBirthday());

    userRepository.save(user);
    userRepository.flush();
  }
  public void setOffline(User token){
    User user = this.userRepository.findByToken(token.getToken());
    user.setStatus(UserStatus.OFFLINE);

    userRepository.save(user);
    userRepository.flush();
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username", "is"));
    }
  }

    public void checkIfUserExistsEdit(User userChanges, Long id) {
        User userToBeEdited = userRepository.findByUserid(id);
        User user = userRepository.findByUsername(userChanges.getUsername());

        String baseErrorMessage = "This %s already exists!";
        if (user != null && !Objects.equals(user.getId(), userToBeEdited.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "username"));
        }

    }

  private void checkIfUserExistsProfile(Long id) {
      User userByID = userRepository.findByUserid(id);

      String baseErrorMessage = "User %s not found";
      if (userByID == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(baseErrorMessage, id));
      }

  }

  public User checkIfUserExistsLogin(User userToBeLoggedIn) {
    User userByUsernameAndName = userRepository.findByUsernameAndName(userToBeLoggedIn.getUsername(), userToBeLoggedIn.getName());
    userByUsernameAndName.setStatus(UserStatus.ONLINE);

    String baseErrorMessage = "The %s provided is not correct. Login failed!";
    if (!Objects.equals(userByUsernameAndName.getUsername(), userToBeLoggedIn.getUsername())){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            String.format(baseErrorMessage, "username"));
    }
    else if (!Objects.equals(userByUsernameAndName.getName(), userToBeLoggedIn.getName())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            String.format(baseErrorMessage, "password"));
    }
    return userByUsernameAndName;
  }
}
