package com.project.user.model.Dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.user.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersDto {
    private long user_id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    private Status status;
}
