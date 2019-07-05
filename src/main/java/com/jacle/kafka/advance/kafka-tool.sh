#create and show kafka topic
kafka-topic.sh --zookeeper localhost:2181 --describe --topic test

#produce and consumer
kafka-console-producer --broker-list s203:9092 --topic test
kafka-console-consumer  --bootstrap-server s203:9092 --topic test

