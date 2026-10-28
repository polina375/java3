
package ru.itmo.web.student;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false, unique = true)
    private String isuId;

    private Integer dormNumber;

    private String room;

    private LocalDate moveInDate;

    private Boolean isForeigner;

    @Column(length = 2000)
    private String notes;

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIsuId() {
        return isuId;
    }

    public void setIsuId(String isuId) {
        this.isuId = isuId;
    }

    public Integer getDormNumber() {
        return dormNumber;
    }

    public void setDormNumber(Integer dormNumber) {
        this.dormNumber = dormNumber;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Boolean getForeigner() {
        return isForeigner;
    }

    public void setForeigner(Boolean foreigner) {
        isForeigner = foreigner;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
