#!/bin/bash

ABSOLUTE_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
profile=$1
container_name="55g-gateway-live"
image_name="55g-gateway-server"
spring_env="live"
server_port=8000

if [ "$profile" == "--dev" ]; then
	container_name="55g-gateway-dev"
	spring_env="dev"
	server_port=8050
fi

cd $ABSOLUTE_PATH

docker_ps=$(docker ps --all --filter "name=${container_name}" | awk '{ print $1 }')

i=0
for line in $docker_ps; do
  ps_arr[i]=$line
  i=$((i+1))
done

for ((i=1; i<${#ps_arr[@]}; i++)); do
    echo "Removing container ${ps_arr[i]}..."
    docker stop ${ps_arr[i]}
    docker rm ${ps_arr[i]}
done

echo "Building docker image..."
docker build -t $image_name .

echo "Creating container for service..."
docker run -d --name $container_name --env SPRING_PROFILE=$spring_env --env SERVER_PORT=$server_port -p $server_port:$server_port $image_name

echo "Pruning images..."
docker image prune --force
