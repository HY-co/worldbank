package com.hanyang.worldbank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Country {
    @Id
    private String code;

    @Column
    private String name;

    @Column
    private Long internetUsers;

    @Column
    private Long adultLiteracyRate;

    public Country() {}

    public Country(CountryBuilder countryBuilder) {
        this.code = countryBuilder.code;
        this.name = countryBuilder.name;
        this.internetUsers = countryBuilder.internetUsers;
        this.adultLiteracyRate = countryBuilder.adultLiteracyRate;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", internetUsers=" + internetUsers +
                ", adultLiteracyRate=" + (adultLiteracyRate == null ? "--" : Math.round(adultLiteracyRate * 100.0) / 100.0) +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(Long internetUsers) {
        this.internetUsers = internetUsers;
    }

    public Long getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(Long adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public static class CountryBuilder {
        private String code;
        private String name;
        private Long internetUsers;
        private Long adultLiteracyRate;

        public CountryBuilder(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public CountryBuilder withInternetUsers(Long internetUsers) {
            this.internetUsers = internetUsers;
            return this;
        }

        public CountryBuilder withAdultLiteratyRate(Long adultLiteracyRate) {
            this.adultLiteracyRate = adultLiteracyRate;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }
}
