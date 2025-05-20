package rw.auca.hr.model;

import java.util.Date;

public class Employee {
    private int empId;
    private String name;
    private String gender;
    private Date startDate;
    private String positionName;
    private String qualification;
    
    public Employee() {}
    
    public Employee(int empId, String name, String gender, Date startDate, 
                   String positionName, String qualification) {
        this.empId = empId;
        this.name = name;
        this.gender = gender;
        this.startDate = startDate;
        this.positionName = positionName;
        this.qualification = qualification;
    }
    
    public int getEmpId() {
        return empId;
    }
    
    public void setEmpId(int empId) {
        this.empId = empId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public String getPositionName() {
        return positionName;
    }
    
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
}
