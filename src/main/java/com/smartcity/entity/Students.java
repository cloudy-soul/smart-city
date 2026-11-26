package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Students - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "studentses")
public class Students {

    @Column(nullable = false)
    private Long studentID;
    @ManyToOne
    @JoinColumn(name = "cv_id")
    protected CV CV;
    @ManyToOne
    @JoinColumn(name = "educational_institution_id")
    private EducationalInstitution educationalInstitution;
    @OneToMany
    @JoinColumn(name = "students_id")
    private List<CV> cvs = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Students() {
    }

    public Students(Long studentID, CV CV) {
        this.studentID = studentID;
        this.CV = CV;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentID() {
        return studentID;
    }

    public void setStudentID(Long studentID) {
        this.studentID = studentID;
    }

    public CV getCV() {
        return CV;
    }

    public void setCV(CV CV) {
        this.CV = CV;
    }

    public EducationalInstitution getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(EducationalInstitution educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }

    public List<CV> getCvs() {
        return Collections.unmodifiableList(cvs);
    }

    public void addCv(CV item) {
        if (item != null && !cvs.contains(item)) {
            cvs.add(item);
        }
    }

    public void removeCv(CV item) {
        cvs.remove(item);
    }

    public void updateStudentProfile() {
        // TODO(diagram2app): the behaviour of Students.updateStudentProfile() is not specified in the diagram.
        throw new UnsupportedOperationException("Students.updateStudentProfile() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "Students{" + "studentID=" + getStudentID() + "}";
    }
}
