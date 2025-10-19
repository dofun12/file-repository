FROM lemanoman/java-21-python:latest

RUN apt-get update
RUN apt-get install -y htop
RUN apt-get install -y bash

RUN mkdir -p /out
RUN mkdir -p /out/files

ENV LOCAL_STORAGE_PATH /out/files


COPY target/*.jar /out/app.jar
COPY docker/entrypoint.sh .
RUN (cd /out ; jar -xf app.jar)
RUN mkdir /app
RUN ls /out/
RUN cp -r /out/BOOT-INF/lib /app/lib
RUN cp -r /out/META-INF /app/META-INF
RUN cp -r /out/BOOT-INF/classes/* /app
RUN chmod +x entrypoint.sh
ENTRYPOINT ./entrypoint.sh
