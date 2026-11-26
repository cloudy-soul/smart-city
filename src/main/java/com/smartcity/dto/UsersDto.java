package com.smartcity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/** Request/response body for Users. References are ids. */
public record UsersDto(
        Long id,
        @NotNull String firstname,
        @NotNull String lastname,
        @NotNull @Email String email,
        @NotNull String phone,
        @NotNull String country,
        @NotNull String city,
        @NotNull String region,
        @NotNull String address,
        @NotNull String nationality,
        @NotNull LocalDate birthday,
        @NotNull String zipcode,
        @NotNull String nationalID,
        @NotNull String userCode,
        Long studentsId,
        Long adminId,
        Long onlineSessionId,
        List<Long> bookingsIds,
        List<Long> jobApplicationsIds,
        Long touristsId) {
}
