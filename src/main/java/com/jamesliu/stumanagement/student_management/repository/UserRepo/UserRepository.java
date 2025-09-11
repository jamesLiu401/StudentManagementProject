package com.jamesliu.stumanagement.student_management.repository.UserRepo;

import com.jamesliu.stumanagement.student_management.Entity.User.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @NonNull
    Optional<User> findByUsername(@NonNull String username);
    boolean existsByUsername(@NonNull String username);
}
