package com.demo.project99.repository.secondary;

import com.demo.project99.domain.secondary.EmployeeRead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecondaryEmployeeRepository extends JpaRepository<EmployeeRead, Long> {
}
