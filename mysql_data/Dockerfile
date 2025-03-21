FROM mariadb:10.5.28
ENV MYSQL_ROOT_PASSWORD=1234
ENV MYSQL_DATABASE=msadb
COPY ./data /var/lib/mysql
EXPOSE 3306

ENTRYPOINT ["mysqld", "--user=root"]