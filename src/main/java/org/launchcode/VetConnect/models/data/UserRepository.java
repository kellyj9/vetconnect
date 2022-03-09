package org.launchcode.VetConnect.models.data;

import org.launchcode.VetConnect.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmailAddress(String emailAddress);

}
