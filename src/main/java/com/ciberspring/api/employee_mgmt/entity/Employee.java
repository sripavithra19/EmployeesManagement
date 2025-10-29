package com.ciberspring.api.employee_mgmt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dps_user")
public class Employee {
    
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dps_user_seq")
    @SequenceGenerator(name = "dps_user_seq", sequenceName = "DPS_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "LOGIN", length = 100, nullable = false)
    private String login;
    
    @Column(name = "FIRST_NAME", length = 40)
    private String firstName;
    
    @Column(name = "LAST_NAME", length = 40)
    private String lastName;
    
    @Column(name = "EMAIL", length = 100)
    private String email;
    
    @Column(name = "HOME_ADDRESS")
    @Lob
    private String homeAddress;
    
    @Column(name = "WORK_ADDRESS")
    @Lob
    private String workAddress;
    
    @Column(name = "PERMANENT_ADDRESS")
    @Lob
    private String permanentAddress;
    
    // Constructors
    public Employee() {}
    
    public Employee(String login, String firstName, String lastName, String email) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
    
    public String getWorkAddress() { return workAddress; }
    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }
    
    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }
}