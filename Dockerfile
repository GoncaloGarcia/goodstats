FROM openjdk:11.0.2-slim

ADD target/lib/lib /app/lib
ADD target/classes /app/classes

WORKDIR /app

ENV JAVA_OPTS ""

ENTRYPOINT ["/usr/bin/java", "-cp", "classes:lib/*", "goodstats.core"]