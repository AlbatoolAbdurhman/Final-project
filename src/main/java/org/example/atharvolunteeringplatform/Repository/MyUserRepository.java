package org.example.atharvolunteeringplatform.Repository;

import org.example.atharvolunteeringplatform.Model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Integer> {

    MyUser findMyUserById(Integer id);

    MyUser findMyUserByEmail(String email);
    MyUser findUserByUsername( String Username);
}