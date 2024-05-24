FROM amazoncorretto:17

ADD build/libs/*.jar /genealogy.jar
#COPY target/genealogy.jar /genealogy.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "genealogy.jar"]