package com.cy.ordersystem.repository;

import com.cy.ordersystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);

    List<User> findByUsernameAndPassword(String username, String password);

    @Modifying
    @Query("update User set token=?1 where username=?2")
    void updateToken(String token, String username);

    @Modifying
    @Query("update User set password=?1 where username=?2")
    void updatePassword(String password, String username);

}
