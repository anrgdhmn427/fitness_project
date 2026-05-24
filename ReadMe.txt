1)Run Docket Desktop
2)Run the RabbitMQ in the local Docker.and paste the command in the powershell/Cmd.

docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

RabbitMq Cred below
login-- guest
passsword -- guest

3) run KeyCloak--
docker run -p 127.0.0.1:8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.6.2 start-dev
create a new realm-- >fitness-oauth2
add new client -- >
    Client_ID:- oauth2-pkce-client

    authentication flow: standard flow and   Direct flow
    Require PKCE S256 ON


how to use Postman for API if Unauthorized Issue,

go to authorization section of postman...
 to Create the token
token name: fitness-app-token
grant type: Authorization code with pkce
auth url: http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth  (taken from realm setting endpoints----> openID endpoint configuration -->authorization_endpoint)
access token url: http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/token (taken from realm setting endpoints----> openID endpoint configuration -->token_endpoint)
client ID:  oauth2-pkce-client (client name in the realm)

then click -> Get new Access token

Login using credentials..then use Token
(you can create the credentials using keyCloak dashboard-->users-->add user)



GOTO authorization Tab:

authType--> OAuth 2
Go TO realm setting to see the endpoints --> Find "Endpoints"
                                then OpenID Endpoint Configuration





3) Add Gemini API credentials in environment variables



