1)Run the RabbitMQ in the local Docker.and paste the command in the powershell.

docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
login-- guest
passsword -- guest




2) Add Gemini API credentials in environment variables