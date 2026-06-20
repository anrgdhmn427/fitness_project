# Fitness Tracking

Microservices backend for tracking workouts and getting AI-powered fitness recommendations.

--> Services

eureka          -> 8761 -> Service discovery
configserver    -> 8888 -> Centralized config
gateway         -> 8080 -> API Gateway + Keycloak JWT validation
userservice     -> 8081 -> PostgreSQL -> User registration & profile
activityservice -> 8082 -> MongoDB    -> Workout logging, publishes to RabbitMQ
aiservice       -> 8083 -> MongoDB    -> Consumes from RabbitMQ, calls Gemini API


--> Working

User authenticates against Keycloak (OAuth2 PKCE) -> gateway validates the JWT -> if first time, a custom `KeyCloakUserSyncFilter` auto-provisions the user in userservice (no separate signup endpoint).

When a workout is logged, activityservice persists to MongoDB and publishes an event to RabbitMQ. aiservice consumes the event, sends a structured prompt to Gemini, parses the response into recommendations (analysis, improvements, next workout suggestions, safety tips), and persists to MongoDB.

Activity logging and AI processing are fully async -- the client gets an immediate response while recommendations generate in the background.


-->Tech Stack

Java 17 -- Spring Boot 3.3.8 -- Spring Cloud (Gateway, Eureka, Config) -- Spring Security 6 -- Keycloak (OAuth2 PKCE / OIDC) -- RabbitMQ -- PostgreSQL -- MongoDB -- Gemini API -- WebClient -- Maven


-->HOW TO RUN

Prerequisites: Java 17+, Maven, Docker, PostgreSQL on 5432, MongoDB on 27017.

--- Start RabbitMQ and Keycloak locally on Docker Desktop

docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

docker run -p 127.0.0.1:8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.6.2 start-dev


RabbitMQ UI: http://localhost:15672 (guest/guest)

---> Configure Keycloak at http://localhost:8181:

Create realm `fitness-oauth2` -> create client `oauth2-pkce-client`, redirect URI `https://oauth.pstmn.io/v1/callback` (Postman's PKCE callback), enable Standard Flow + Direct Access Grants, turn on PKCE S256. Then create a test user.


--> Environment variables (PowerShell)

$env:GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
$env:GEMINI_API_KEY="your_key"


-> Start services (in order)

eureka -> configserver -> userservice -> activityservice -> aiservice -> gateway


--->Endpoints

POST /api/users/register
GET  /api/users/{userId}
GET  /api/users/{userId}/validate

POST /api/activities
GET  /api/activities
GET  /api/activities/{activityId}

GET /api/recommendations/user/{userId}
GET /api/recommendations/activity/{activityId}


-->Postman Testing

Auth tab -> OAuth 2.0 -> Authorization Code with PKCE
Auth URL:     http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth
Token URL:    http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/token
Callback URL: https://oauth.pstmn.io/v1/callback
Client ID:    oauth2-pkce-client

Click `Get New Access Token`.