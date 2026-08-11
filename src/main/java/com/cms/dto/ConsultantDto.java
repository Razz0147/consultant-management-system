package com.cms.dto;

import jakarta.validation.constraints.*;

public class ConsultantDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$|^\\+?[0-9\\s\\-\\(\\)]{7,20}$", 
             message = "Please enter a valid phone number (e.g. +1 555-0101 or 5550101010)")
    private String phone;

    @NotBlank(message = "Technology stack is required")
    private String technology;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experience;

    @NotBlank(message = "Status is required")
    private String status;

    public ConsultantDto() {
    }

    public ConsultantDto(Long id, String name, String email, String phone, String technology, Integer experience, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.technology = technology;
        this.experience = experience;
        this.status = status;
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

    public static ConsultantDtoBuilder builder() {
        return new ConsultantDtoBuilder();
    }

    public static class ConsultantDtoBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String technology;
        private Integer experience;
        private String status;

        public ConsultantDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConsultantDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ConsultantDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ConsultantDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ConsultantDtoBuilder technology(String technology) {
            this.technology = technology;
            return this;
        }

        public ConsultantDtoBuilder experience(Integer experience) {
            this.experience = experience;
            return this;
        }

        public ConsultantDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ConsultantDto build() {
            return new ConsultantDto(id, name, email, phone, technology, experience, status);
        }
    }
}
