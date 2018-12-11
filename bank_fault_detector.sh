#!/bin/sh
export PATH=$PATH:/Users/Diego/zookeeper-3.4.13/bin/ # Set here your zookeeper binaries folder
zkCli.sh create /members midata
while true
do
	MEMBERS=$(sh zkCli.sh ls /members | sed '/member/ !c\' | sed 's/member-00000000//g' | wc -c)
	echo $MEMBERS
	if [ $MEMBERS -lt 10 ]
		then 
			osascript -e 'tell app "Terminal" 
				do script "java -jar /Users/Diego/git/Zookeeper_Bank/zookeeper_bank.jar" # Set your JAR path
				end tell' # For MAC
			# xterm -hold -e java -jar zookeeper_bank.jar & For linux
			sleep 10s
		else 
			echo There are enough servers.
			sleep 10s
		fi
done

