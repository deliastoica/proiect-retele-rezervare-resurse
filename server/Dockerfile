FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copiem fisierele din folderul local de server in imaginea Docker
COPY . /app/server

# Compilam toate fisierele din pachet
RUN javac server/*.java

EXPOSE 5000

# Pornim clasa principala din package-ul server
CMD ["java", "server.MainServer"]