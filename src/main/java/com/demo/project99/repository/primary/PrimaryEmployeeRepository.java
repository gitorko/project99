package com.demo.project99.repository.primary;


import com.demo.project99.domain.primary.EmployeeWrite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrimaryEmployeeRepository extends JpaRepository<EmployeeWrite, Long> {
}
