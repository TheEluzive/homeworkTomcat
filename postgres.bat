docker container run -p 5555:5432 -e POSTGRES_USER=app -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=app -v "E:\HW\homeworkTomcat\docker-entrypoint-initdb.d":/docker-entrypoint-initdb.d postgres 

pause