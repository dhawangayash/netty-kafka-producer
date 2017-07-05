#!/bin/bash
counter=0
while true; 
do
counter=$((counter+1))
echo $counter

curl -X POST http://0.0.0.0:8080/ -H 'cache-control: no-cache' -d '{$LOAD_DATA}'
done

