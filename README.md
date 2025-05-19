# my_blog

mvn test -Dtest=PostControllerTest,AnotherTestClass // запуск тестов
mvn -DskipTests=true clean package // сборка war
Сборка проекта:
1. mvn -DskipTests=true clean package // сборка war
2. положить в веб-сервер сервлетов tomcat