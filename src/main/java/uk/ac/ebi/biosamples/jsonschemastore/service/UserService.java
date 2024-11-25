package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;
import uk.ac.ebi.biosamples.jsonschemastore.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Invalid username or password"));
  }

  @Override
  public void createUser(UserDetails user) {
    userRepository.insert((User)user);
  }

  @Override
  public void updateUser(UserDetails user) {
    User updatedUser = userRepository.findByUsername(user.getUsername())
            .map(
                    dbUser -> {
                       ((User) user).setId(dbUser.getId());
                      return (User)user;
                    }
            ).orElse((User)user);
    userRepository.save(updatedUser);
  }

  @Override
  public void deleteUser(String username) {
    throw new NotImplementedException("deleteUser is not implemented");
  }

  @Override
  public void changePassword(String oldPassword, String newPassword) {
    throw new NotImplementedException("changePassword is not implemented. It is done through webin");
  }

  @Override
  public boolean userExists(String username) {
    return userRepository.findByUsername(username).isPresent();
  }
}
