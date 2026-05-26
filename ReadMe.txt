# Fitness Tracking 

Microservices backend for tracking workouts and getting AI-powered fitness recommendations
--> Services


eureka ->8761 -->Service discovery
configserver-> 8888 -> Centralized config
gateway ->8080 -> API Gateway + Keycloak JWT validation
userservice->8081->PostgreSQL->User registration & profile
activityservice-> 8082 ->MongoDB->Workout logging, publishes to RabbitMQ
aiservice-> 8083 ->MongoDB->Consumes from RabbitMQ, calls Gemini API 





--> Working

User logs in through Keycloak->gateway validates the JWT->if first time user, the gateway auto-registers them (wrote a custom `KeyCloakUserSyncFilter` for this so there's no separate signup step).

When user logs a workout, it gets saved in MongoDB and an event goes to RabbitMQ. The AI service picks it up, sends it to Gemini with a structured prompt, parses the response into recommendations (analysis, improvements, next workout suggestions, safety tips) and saves to MongoDB.

Activity logging and AI processing are completely async --user gets immediate response, recommendations generate in the background.

-->Tech Stack



-->HOW TO RUN



--- STart RabbitMQ and Keyclock Locally on Docket desktop

PowerShell

docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

docker run -p 127.0.0.1:8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.6.2 start-dev


RabbitMQ UI: http://localhost:15672 (guest/guest)

---> Configure Keycloak at http://localhost:8181:



--> Set Environment users

Powershell

export GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
export GEMINI_API_KEY=your_key


->Start services


eureka->configserver->userservice->activityservice->aiservice->gateway











--->Endpoints


Users
POST /api/users/register
GET /api/users/{userId}
GET /api/users/{userId}/validate


POST /api/activities
GET /api/activities
GET /api/activities/{activityId}


GET /api/recommendations/user/{userId}
GET /api/recommendations/activity/{activityId}

-->Postman Testing

Auth tab->OAuth 2.0->Authorization Code with PKCE
Auth URL: http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth
Token URL: http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/token
Client ID: oauth2-pkce-client

click Get New Access Token
