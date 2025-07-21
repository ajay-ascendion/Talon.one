package com.app.repository;

import com.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and query methods for User data.
 * 
 * This interface extends JpaRepository, enabling standard data access methods
 * such as save, findById, findAll, deleteById, etc., for the User entity.
 * 
 * No custom query methods are defined, adhering to Spring Data JPA conventions.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Default query methods provided by JpaRepository are sufficient.
}
