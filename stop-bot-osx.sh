#!/bin/bash

source config.sh

kill `cat ${pid_file}`
