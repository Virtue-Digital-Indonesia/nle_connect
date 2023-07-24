#!/bin/bash
sql_slave_user='CREATE USER "root"@"%" IDENTIFIED BY "root";
GRANT REPLICATION SLAVE ON *.* TO "root"@"%";
FLUSH PRIVILEGES;'
docker exec mysql-master sh -c "mysql -u root -p$DB_PASSWORD -e '$sql_slave_user'"
MS_STATUS=`docker exec mysql-master sh -c 'mysql -u root -p$DB_PASSWORD -e "SHOW MASTER STATUS"'`
CURRENT_LOG=`echo $MS_STATUS | awk '{print $6}'`
CURRENT_POS=`echo $MS_STATUS | awk '{print $7}'`
sql_set_master="CHANGE MASTER TO MASTER_HOST='mysql-master',MASTER_USER='root',MASTER_PASSWORD=$DB_PASSWORD,MASTER_LOG_FILE='$CURRENT_LOG',MASTER_LOG_POS=$CURRENT_POS; START SLAVE;"
start_slave_cmd='mysql -u root -p$DB_PASSWORD -e "'
start_slave_cmd+="$sql_set_master"
start_slave_cmd+='"'
docker exec mysql-slave sh -c "$start_slave_cmd"
docker exec mysql-slave sh -c "mysql -u root -p$DB_PASSWORD -e 'SHOW SLAVE STATUS \G'"