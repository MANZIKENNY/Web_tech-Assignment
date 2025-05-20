package com.auca.ems.controller;

import com.auca.ems.model.Employee;
import com.auca.ems.model.User;
import com.auca.ems.service.EmployeeService;
import com.auca.ems.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public String listEmployees(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("userRole", currentUser.getRole());
        return "employees/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.UserRole.HR && currentUser.getRole() != User.UserRole.ADMIN) {
            return "redirect:/access-denied";
        }
        
        model.addAttribute("employee", new Employee());
        return "employees/add";
    }
    
    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee, 
                             @RequestParam("profileImage") MultipartFile profileImage,
                             RedirectAttributes redirectAttributes) {
        try {
            if (!profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                employee.setProfilePicture(fileName);
            }
            
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("message", "Employee added successfully!");
            return "redirect:/employees";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture: " + e.getMessage());
            return "redirect:/employees/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.UserRole.HR && currentUser.getRole() != User.UserRole.ADMIN) {
            return "redirect:/access-denied";
        }
        
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        return "employees/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute Employee employee,
                                @RequestParam("profileImage") MultipartFile profileImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Employee existingEmployee = employeeService.findById(id);
            employee.setId(id);
            
            if (!profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                employee.setProfilePicture(fileName);
            } else {
                employee.setProfilePicture(existingEmployee.getProfilePicture());
            }
            
            employeeService.save(employee);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
            return "redirect:/employees";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture: " + e.getMessage());
            return "redirect:/employees/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRole() != User.UserRole.HR && currentUser.getRole() != User.UserRole.ADMIN) {
            return "redirect:/access-denied";
        }
        
        employeeService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        return "redirect:/employees";
    }
    
    @GetMapping("/search")
    public String searchEmployees(@RequestParam String keyword, Model model) {
        List<Employee> employees = employeeService.findByNameContainingIgnoreCase(keyword);
        model.addAttribute("employees", employees);
        return "employees/list";
    }
    
    @GetMapping("/filter")
    public String filterByDepartment(@RequestParam String department, Model model) {
        List<Employee> employees = employeeService.findByDepartment(department);
        model.addAttribute("employees", employees);
        return "employees/list";
    }
}
