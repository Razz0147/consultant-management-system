package com.cms.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultants")
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String technology;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false, length = 20)
    private String status; // "Active" or "Inactive"

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Consultant() {
    }

    public Consultant(Long id, String name, String email, String phone, String technology, Integer experience, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.technology = technology;
        this.experience = experience;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static ConsultantBuilder builder() {
        return new ConsultantBuilder();
    }

    public static class ConsultantBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String technology;
        private Integer experience;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ConsultantBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConsultantBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ConsultantBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ConsultantBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ConsultantBuilder technology(String technology) {
            this.technology = technology;
            return this;
        }

        public ConsultantBuilder experience(Integer experience) {
            this.experience = experience;
            return this;
        }

        public ConsultantBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ConsultantBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ConsultantBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Consultant build() {
            return new Consultant(id, name, email, phone, technology, experience, status, createdAt, updatedAt);
        }
    }
}
