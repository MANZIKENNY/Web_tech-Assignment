package com.auca.ems.service;

import com.auca.ems.model.Employee;
import com.auca.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
    
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
    
    public List<Employee> findByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    public List<Employee> findByNameContainingIgnoreCase(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }
}
