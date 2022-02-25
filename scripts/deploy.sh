#!/bin/bash

REPOSITORY=/home/ec2-user
echo "> 이미 존재하는 중복 파일 삭제"
rm $REPOSITORY/*.jar
rm $REPOSITORY/deploy.log

JAR_PATH=$(ls -tr /home/ec2-user/jenkins/build/libs/*.jar | grep SNAPSHOT.jar | tail -n 1)
JAR_NAME=$(basename $JAR_PATH)

echo "> build 파일명: $JAR_NAME" >> $REPOSITORY/deploy.log
echo "> build 파일 복사" >> $REPOSITORY/deploy.log
DEPLOY_PATH=/home/ec2-user/
cp $JAR_PATH $DEPLOY_PATH$JAR_NAME

echo "> 현재 실행 중인 애플리케이션 pid 확인" >> $REPOSITORY/deploy.log
CURRENT_PID=$(pgrep -f sensomedi.*.SNAPSHOT.jar)

echo "> 현재 실행 중인 애플리케이션 pid: $CURRENT_PID" >> $REPOSITORY/deploy.log

if [ -z "${CURRENT_PID}" ]
then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다." >> $REPOSITORY/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> $REPOSITORY/deploy.log
  kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "> 새 애플리케이션 배포" >> $REPOSITORY/deploy.log

cd $REPOSITORY

echo "> 디렉토리 이동 $REPOSITORY" >> $REPOSITORY/deploy.log

chmod +x $JAR_NAME

nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,classpath:/application-real-db.properties,classpath:/application-mail.properties \
        -Dspring.profiles.active=real \
        $REPOSITORY/$JAR_NAME >/dev/null 2>&1 &

echo "> 배포 완료" >> $REPOSITORY/deploy.log