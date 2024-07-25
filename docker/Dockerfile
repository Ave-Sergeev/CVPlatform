FROM eclipse-temurin:21-jammy
ENV USER_INSTALL_HOME="/app"
RUN mkdir -p ${USER_INSTALL_HOME}/temp
ENV PROJECT_DIR=${USER_INSTALL_HOME}
RUN mkdir -p /etc
WORKDIR ${PROJECT_DIR}
COPY target/pack/ ${PROJECT_DIR}/
ENV TZ=Europe/Moscow

CMD ${PROJECT_DIR}/bin/main
