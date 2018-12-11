#!/bin/sh
while true
do
	MEMBERS=$(sh zkCli.sh ls /members | sed '/member/ !c\' | sed 's/member-00000000//g' | wc -c)
	if [ $MEMBERS -lt 10 ]
		then 
			osascript -e 'tell app "Terminal" 
				do script "java -jar zookeeper_bank.jar" 
				end tell'
			sleep 30s
		else 
			echo There are enough servers.
			sleep 30s
		fi
done

