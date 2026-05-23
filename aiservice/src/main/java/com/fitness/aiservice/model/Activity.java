package com.fitness.aiservice.model;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {

    private String id;
    private String userID;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrices;
    private LocalDateTime crtAt;
    private LocalDateTime updtAt;
    private Recommendation recommendation;

}
