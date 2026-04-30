# Usa uma imagem base do Tomcat 10 com Java 17
FROM tomcat:10.1-jdk17

# Remove aplicações padrão
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o .war construído
COPY target/transcarga.war /usr/local/tomcat/webapps/ROOT.war

# Copia o driver do MariaDB (se necessário)
COPY lib/mariadb-java-client-3.3.0.jar /usr/local/tomcat/lib/

# Porta exposta
EXPOSE 8080

# Comando para iniciar o Tomcat
CMD ["catalina.sh", "run"]