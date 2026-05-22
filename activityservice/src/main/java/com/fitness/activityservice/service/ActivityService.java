package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;

    @Value("${rabbitmq.exchange-name}")
    private String exchange;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;


    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUSer=userValidationService.validateUser(request.getUserId());
        if(!isValidUSer){
            throw new RuntimeException("Invalid User" + request.getUserId());
        }
        Activity activity= Activity.builder()
                .userID(request.getUserId())
                .type((request.getType()))
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrices(request.getAdditionalMetrics())
                .build();

        Activity saveActivity = activityRepository.save(activity);

        // Publish to RabbitMQ for AI processing

        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,saveActivity);
        } catch (Exception e) {
            log.error("Failed to publish  to RabbitMQ ", e);
            throw new RuntimeException(e);
        }



        return mapToResponse(saveActivity);
    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response= new ActivityResponse();
        response.setId(activity.getId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCrtAt(activity.getCrtAt());
        response.setAdditionalMetrices(activity.getAdditionalMetrices());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setUpdtAt(activity.getUpdtAt());
        response.setUserID(activity.getUserID());
        response.setStartTime(activity.getStartTime());


        return response;
    }

    public List<ActivityResponse> getUserActivities(String userID) {
        List<Activity> activities= activityRepository.findByUserID(userID);
        return activities.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {

        return activityRepository.findById(activityId).map(this::mapToResponse).orElseThrow(()-> new RuntimeException(("activity not found")));
    }
}
