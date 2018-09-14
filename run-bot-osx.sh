#!/bin/bash

./CodeBattleJava/gradlew build -p CodeBattleJava

source config.sh

> ${pid_file}

for (( i=1; i<=$players_count; i++ ))
do
	echo "Run client ${i} with code ${player_codes[$i]} on ${server}"
	nohup java \
	-Duser=test$i@test.com \
	-Dcode=${player_codes[$i]} \
	-Dserver=${server} \
	-jar CodeBattleJava/build/libs/battlecity-client-1.0.jar \
	 &>/dev/null &
	 echo "$!" >> ${pid_file}
done


