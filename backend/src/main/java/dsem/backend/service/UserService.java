package dsem.backend.service;

import dsem.backend.model.entity.User;
import dsem.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User toggleActive(Long id) {
        User user = getById(id);
        user.setActive(!user.getActive());
        return userRepository.save(user);
    }
}
