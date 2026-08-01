package com.bank.domain.client;

import com.bank.domain.common.ClientId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Client {
    
    private final ClientId id;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final LocalDate birthDate;
    private final String passportSeries;
    private final String passportNumber;
    private final String inn;
    private final String snils;
    private final String phoneNumber;
    private final String email;
    private final ClientType type;
    private final RiskProfile riskProfile;
    private final boolean isResident;
    private final String citizenship;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private boolean active;
    
    private Client(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "ClientId обязателен");
        this.firstName = Objects.requireNonNull(builder.firstName, "Имя обязательно");
        this.lastName = Objects.requireNonNull(builder.lastName, "Фамилия обязательна");
        this.middleName = builder.middleName;
        this.birthDate = builder.birthDate;
        this.passportSeries = builder.passportSeries;
        this.passportNumber = builder.passportNumber;
        this.inn = builder.inn;
        this.snils = builder.snils;
        this.phoneNumber = builder.phoneNumber;
        this.email = builder.email;
        this.type = builder.type != null ? builder.type : ClientType.INDIVIDUAL;
        this.riskProfile = builder.riskProfile != null ? builder.riskProfile : RiskProfile.LOW;
        this.isResident = builder.isResident;
        this.citizenship = builder.citizenship != null ? builder.citizenship : "RU";
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : LocalDateTime.now();
        this.active = true;
    }
    
    public static Builder builder() { return new Builder(); }
    
    public ClientId getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPassportSeries() { return passportSeries; }
    public String getPassportNumber() { return passportNumber; }
    public String getInn() { return inn; }
    public String getSnils() { return snils; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public ClientType getType() { return type; }
    public RiskProfile getRiskProfile() { return riskProfile; }
    public boolean isResident() { return isResident; }
    public String getCitizenship() { return citizenship; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public boolean isActive() { return active; }
    
    public void deactivate() { this.active = false; }
    
    public void activate() { this.active = true; }
    
    public String getFullName() {
        return String.format("%s %s %s", lastName, firstName,
            middleName != null ? middleName : "").trim();
    }
    
    public boolean isHighRisk() {
        return riskProfile == RiskProfile.HIGH || riskProfile == RiskProfile.PEP;
    }
    
    public boolean isSanctioned() {
        return riskProfile == RiskProfile.SANCTIONED || riskProfile == RiskProfile.BLACKLISTED;
    }
    
    public static class Builder {
        private ClientId id;
        private String firstName;
        private String lastName;
        private String middleName;
        private LocalDate birthDate;
        private String passportSeries;
        private String passportNumber;
        private String inn;
        private String snils;
        private String phoneNumber;
        private String email;
        private ClientType type;
        private RiskProfile riskProfile;
        private boolean isResident = true;
        private String citizenship;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(ClientId id) { this.id = id; return this; }
        public Builder firstName(String v) { this.firstName = v; return this; }
        public Builder lastName(String v) { this.lastName = v; return this; }
        public Builder middleName(String v) { this.middleName = v; return this; }
        public Builder birthDate(LocalDate v) { this.birthDate = v; return this; }
        public Builder passportSeries(String v) { this.passportSeries = v; return this; }
        public Builder passportNumber(String v) { this.passportNumber = v; return this; }
        public Builder inn(String v) { this.inn = v; return this; }
        public Builder snils(String v) { this.snils = v; return this; }
        public Builder phoneNumber(String v) { this.phoneNumber = v; return this; }
        public Builder email(String v) { this.email = v; return this; }
        public Builder type(ClientType v) { this.type = v; return this; }
        public Builder riskProfile(RiskProfile v) { this.riskProfile = v; return this; }
        public Builder isResident(boolean v) { this.isResident = v; return this; }
        public Builder citizenship(String v) { this.citizenship = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        
        public Client build() {
            return new Client(this);
        }
    }
}