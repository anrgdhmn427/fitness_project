package com.fitness.activityservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId){
        try{
            log.info("Calling user Validation API for user iD {}",userId);

            boolean flag = Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());

            return flag;
        }

        catch (WebClientResponseException e){
            if(e.getStatusCode()== HttpStatus.NOT_FOUND){
                log.error("Runtime Exception");
                throw new RuntimeException(("User not found"+ userId));
            }
            if(e.getStatusCode()== HttpStatus.BAD_REQUEST){
                throw new RuntimeException(("user bAD request"+ userId));
            }
        }
        return false;
    }
}
