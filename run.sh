n=0

COMMAND="$1"
shift  # Remove first argument from $@

PORT=8080

#parse command line opts
while [[ $# -gt 0 ]]; do
  case $1 in
    --port=*)
      PORT="${1#*=}"
      shift
      ;;
    --port)
      PORT="$2"
      shift 2
      ;;
    *)
      shift
      ;;
  esac
done

echo "Command: $COMMAND"
echo "Using port: $PORT"

if [ -z "$COMMAND" ]; then
  echo "Usage: $0
      ***************************************************************************************************
      | iPreach commands | Description                                                                  |
      ***************************************************************************************************
      | → create-local   | Build and launch project without Docker (it uses an h2 inmemory db)          |
      | → create         | Build and launch project without API, just dependencies of the backend (API) |
      | → restart        | Just launch project without API, just dependencies of the backend (API)      |
      | → create-api     | Build and launch project with API, just dependencies of the backend (API)    |
      | → restart-api    | Just launch project with API, just dependencies of the backend (API)         |
      |-------------------------------------------------------------------------------------------------|
      "
  exit 1
fi

case "$COMMAND" in
  create-local)
    mvn clean install -DskipTests
    TOMCAT_PORT=$PORT LOAD_INITIAL_DATA=true DB_START_MODE=create mvn spring-boot:run
    exit 1
    ;;
esac

# Open Docker, only if is not running
if (! docker ps ); then
  read -rp 'Docker is not started, do you want for this script to try to start it? [Y/n] ' start_docker
  start_docker={$start_docker:-yes}

  if [[ "$start_docker" =~ [yY].* ]]; then
    # On Mac OS this would be the terminal command to launch Docker
    open -a Docker
  else
    echo "Please, run Docker, you have 30 seconds... ;)"
  fi

  # Wait until Docker daemon is running and has completed initialisation
  while [ "$n" -le 10 ]; do
    # Docker takes a few seconds to initialize
    echo "Waiting 3s for Docker to launch... (if not launched automatically, do it manually)"
    sleep 3
    if(docker stats --no-stream ); then
      break
    fi
    n=$(( n+1 ))
  done
  if [ "$n" -eq 10 ]; then
    echo "Please run Docker first"
    exit 1
  fi
fi

if [ ! -f target ]; then
  rm -rf target
else
  echo "it exists"
fi

#git pull
docker compose down -v
docker compose rm -f
docker rm ipreach-backend-api -f
docker rmi ipreach-backend-api -f

DB_START_MODE=none
LOAD_INITIAL_DATA=false
#TEST_DATA=false

case "$COMMAND" in
  create|create-api)
    purge
    DB_START_MODE=create
    LOAD_INITIAL_DATA=true
    ;;
  restart|restart-api)
    DB_START_MODE=none
    LOAD_INITIAL_DATA=false
    ;;
  purge)
    docker compose -f docker-compose-no-api.yml down
    docker compose -f docker-compose.yml down
    docker rmi postgres -f
    docker rmi maildev/maildev -f
    docker rmi ipreach-backend-api -f
    rm -rf ./data
    set -e
    EXIT_CODE=0
    docker network rm ipreach_api_network || EXIT_CODE=$?
    echo "Network rm: " $EXIT_CODE
    set +e
    exit 1
    ;;
esac

JAVA_OPTS="-XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

echo "DB start mode = $DB_START_MODE"
echo "Load initial data = $LOAD_INITIAL_DATA"

echo "Docker initialization mode = '$COMMAND'"
case "$COMMAND" in
  create|restart)
    DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA JAVA_OPTS=$JAVA_OPTS docker compose -f docker-compose-no-api.yml up -d
  ;;
  create-api|restart-api)
    DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA JAVA_OPTS=$JAVA_OPTS docker compose -f docker-compose.yml up -d
  ;;
esac

case "$COMMAND" in
  create-api|create-local|restart-api)
    echo "ready to work with API @ http://localhost:8080/api, better from postman hehe"
    echo "check api is alive in http://localhost:8080/api/alive"
    echo "check swagger @ http://localhost:8080/api/v1/swagger-ui/index.html"
  ;;
esac
