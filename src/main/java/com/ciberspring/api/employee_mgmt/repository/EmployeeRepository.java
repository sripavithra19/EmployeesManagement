package com.ciberspring.api.employee_mgmt.repository;

import com.ciberspring.api.employee_mgmt.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByLogin(String login);
    
    // Fix for Oracle - use native query or count
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.login = :login")
    boolean existsByLogin(@Param("login") String login);
    
    // Alternative native query approach
    @Query(value = "SELECT COUNT(*) FROM dps_user WHERE login = :login", nativeQuery = true)
    int countByLogin(@Param("login") String login);
    
    // Find the maximum ID for generating next ID
    @Query("SELECT MAX(e.id) FROM Employee e")
    Integer findMaxId();
}
