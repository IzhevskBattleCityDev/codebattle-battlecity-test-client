#!/bin/bash

source config.sh

for (( i=1; i<=$players_count; i++ ))
do
	curl -H "Content-Type: application/x-www-form-urlencoded" \
	-d "name=test${i}@test.com&password=${player_registration_password_hash}&gameName=battlecity" \
	-X POST http://${server}/codenjoy-contest/register
done
