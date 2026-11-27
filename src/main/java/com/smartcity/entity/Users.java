package com.smartcity.entity;

import com.smartcity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Users - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "userses")
public class Users extends BaseEntity {

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Email
    @Column(nullable = false)
    private String email;
    @NotNull
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String nationality;
    @Column
    private LocalDate birthday;
    @NotNull
    @Column(nullable = false)
    private String zipcode;
    @Column(nullable = false)
    private String nationalID;
    @NotNull
    @Column(nullable = false)
    private String userCode;
    @ManyToOne
    @JoinColumn(name = "students_id")
    private Students students;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
    @ManyToOne
    @JoinColumn(name = "online_session_id")
    private OnlineSession onlineSession;
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<JobApplication> jobApplications = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "tourists_id")
    private Tourists tourists;
    @Embedded
    private Account account;
    @ElementCollection
    private List<Role> roles = new ArrayList<>();

    protected Users() {
    }

    public Users(String firstname, String lastname, String email, String phone, String country, String city, String region, String address, String nationality, LocalDate birthday, String zipcode, String nationalID, String userCode, Account account) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.region = region;
        this.address = address;
        this.nationality = nationality;
        this.birthday = birthday;
        this.zipcode = zipcode;
        this.nationalID = nationalID;
        this.userCode = userCode;
        this.account = account;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Column(nullable = false)
    private boolean studentActive = true;

    @Column(nullable = false)
    private int credentialVersion;

    public boolean isActiveStudent() {
        return students != null && studentActive;
    }

    public void setStudentActive(boolean active) {
        this.studentActive = active;
    }

    public int getCredentialVersion() {
        return credentialVersion;
    }

    public void invalidateCredentials() {
        credentialVersion++;
    }

    public Students getStudents() {
        return students;
    }

    public void setStudents(Students students) {
        this.students = students;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public OnlineSession getOnlineSession() {
        return onlineSession;
    }

    public void setOnlineSession(OnlineSession onlineSession) {
        this.onlineSession = onlineSession;
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public void addBooking(Booking item) {
        if (item != null && !bookings.contains(item)) {
            bookings.add(item);
            item.setUser(this);
        }
    }

    public void removeBooking(Booking item) {
        if (bookings.remove(item)) {
            item.setUser(null);
        }
    }

    public List<JobApplication> getJobApplications() {
        return Collections.unmodifiableList(jobApplications);
    }

    public void addJobApplication(JobApplication item) {
        if (item != null && !jobApplications.contains(item)) {
            jobApplications.add(item);
            item.setUser(this);
        }
    }

    public void removeJobApplication(JobApplication item) {
        if (jobApplications.remove(item)) {
            item.setUser(null);
        }
    }

    public Tourists getTourists() {
        return tourists;
    }

    public void setTourists(Tourists tourists) {
        this.tourists = tourists;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public void addRole(Role item) {
        if (item != null && !roles.contains(item)) {
            roles.add(item);
        }
    }

    public void removeRole(Role item) {
        roles.remove(item);
    }

    public String getFullName() {
        return this.firstname;
    }

    public void updateContactDetails(String phone, String address) {
        
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Users{" + "id=" + getId() + ", " + "firstname=" + getFirstname() + ", " + "lastname=" + getLastname() + ", " + "email=" + getEmail() + ", " + "phone=" + getPhone() + ", " + "country=" + getCountry() + ", " + "city=" + getCity() + ", " + "region=" + getRegion() + ", " + "address=" + getAddress() + ", " + "nationality=" + getNationality() + ", " + "birthday=" + getBirthday() + ", " + "zipcode=" + getZipcode() + ", " + "nationalID=" + getNationalID() + ", " + "userCode=" + getUserCode() + "}";
    }
}
