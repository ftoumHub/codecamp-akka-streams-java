## Code Camp Akka Streams Java

Code Camp about using Akka Streams via its Java DSL.

### Kafka

Kafka is used to persist streams.

##### Start Kafka

Open a shell and go to the root directory of this project.

Set the variable SERVER_IP_OR_HOST in your shell to:
* localhost if you are using Linux or Windows
* the IP of your machine if you are using Docker for Mac

by using the command `export SERVER_IP_OR_HOST=<value>`.

In the source code change `constants.Constants.KAFKA_IP_OR_HOST` to the same value.

Launch with the command `docker-compose up -d`

##### See Logging Output

Use the command described at https://docs.docker.com/engine/reference/commandline/logs/

##### List Topics

`docker exec -ti codecamp-akka-streams-java-kafka-01-c /usr/bin/kafka-topics --list --zookeeper codecamp-akka-streams-java-zookeeper4kafka-01-s:44776`

##### Describe a Topic

`docker exec -ti codecamp-akka-streams-java-kafka-01-c /usr/bin/kafka-topics --describe --zookeeper codecamp-akka-streams-java-zookeeper4kafka-01-s:44776 -topic kata06-topic`

##### Create a Topic

`docker exec -ti codecamp-akka-streams-java-kafka-01-c /usr/bin/kafka-topics --create --zookeeper codecamp-akka-streams-java-zookeeper4kafka-01-s:44776 --topic kata06-topic --partitions 1 --replication-factor 1`

##### Stop Kafka and remove all Docker volumes and thus wipe all data

`docker-compose down -v`

##### Stop Kafka keeping data

`docker-compose down`
