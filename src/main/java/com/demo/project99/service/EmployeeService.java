package com.demo.project99.service;

import java.util.List;

import com.demo.project99.domain.primary.EmployeeWrite;
import com.demo.project99.domain.secondary.EmployeeRead;
import com.demo.project99.repository.primary.PrimaryEmployeeRepository;
import com.demo.project99.repository.secondary.SecondaryEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    final PrimaryEmployeeRepository primaryEmployeeRepository;

    final SecondaryEmployeeRepository secondaryEmployeeRepository;

    public EmployeeWrite saveEmployee(EmployeeWrite employeeWrite) {
        return primaryEmployeeRepository.save(employeeWrite);
    }

    public List<EmployeeRead> getAllEmployees() {
        return secondaryEmployeeRepository.findAll();
    }
}
