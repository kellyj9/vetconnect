package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class VetConnectController {

    @Autowired
    UserRepository userRepository;

    public User getUserFromSession(HttpSession session) {
        if(session == null) {
            return null;
        }

        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            return null;
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            return null;
        }

        return user.get();
    }

}
