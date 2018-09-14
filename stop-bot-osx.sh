#!/bin/bash

kill $(ps aux | grep CodeBattleJava | grep -v grep | awk '{print $2}')
