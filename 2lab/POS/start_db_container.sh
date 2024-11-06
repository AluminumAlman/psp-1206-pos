#!/bin/bash

if [ "$(sudo docker ps -q -f name=pos_db_container)" ]; then
    echo "Container 'pos_db_container' is already running."
elif [ "$(sudo docker ps -aq -f name=pos_db_container)" ]; then
    echo "Container 'pos_db_container' exists but is stopped. Starting it..."
    sudo sudo docker start pos_db_container
else
    echo "Container 'pos_db_container' does not exist. Creating and starting it..."
    sudo sudo docker run --name pos_db_container -p 5432:5432 -e POSTGRES_USER=datauser -e POSTGRES_PASSWORD=6y3wxsnq -e POSTGRES_DB=pos -d postgres:15-alpine
fi