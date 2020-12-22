FROM openjdk:11.0.2-slim

ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

RUN apt update && apt upgrade -y
RUN apt install -y curl && apt-get install sudo -y

RUN curl -O https://download.clojure.org/install/linux-install-1.10.1.763.sh
RUN chmod +x linux-install-1.10.1.763.sh
RUN sudo ./linux-install-1.10.1.763.sh

COPY deps.edn $APP_HOME
COPY src $APP_HOME/src
COPY build $APP_HOME/build

RUN clojure -A:build -m package

ENV CLIENT_ID
ENV CLIENT_SECRET

CMD ["/usr/bin/java", "-Xmx256m", "-cp", "target/classes:target/lib/lib/*", "goodstats.core"]
