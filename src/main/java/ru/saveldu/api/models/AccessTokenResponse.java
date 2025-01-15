package ru.saveldu.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class AccessTokenResponse {


    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_at")
    private long expiresAt;

}
