package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
// listen to the rabbit msgs

    private final ActivityAIService activityAIService;
    @Value("${rabbitmq.queue-name}")
    private String queue;
    @Value("${rabbitmq.exchange-name}")
    private String exchange;
    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @RabbitListener(queues = "activity.queue")
    void processActivity(Activity activity) {

        log.info("Received activty for the processing : {}", activity.getId());
        log.info("Generated Recommendation: {}", activityAIService.generatedRecommendation(activity));

    }


}
