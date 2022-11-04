FROM openjdk:18
COPY ./build/libs/* ./app.jar
CMD ["java","-jar","app.jar"]