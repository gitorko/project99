package com.demo.project99.controller;

import java.util.List;

import com.demo.project99.domain.primary.EmployeeWrite;
import com.demo.project99.domain.secondary.EmployeeRead;
import com.demo.project99.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    final EmployeeService employeeService;

    @PostMapping("/employee")
    public EmployeeWrite createEmployee(@RequestBody EmployeeWrite employeeWrite) {
        return employeeService.saveEmployee(employeeWrite);
    }

    @GetMapping("/employee")
    public List<EmployeeRead> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

}
