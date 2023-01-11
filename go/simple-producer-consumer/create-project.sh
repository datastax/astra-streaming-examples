#sudo apt install -y golang-go
mkdir SimpleProducerConsumer && cd SimpleProducerConsumer

go mod init SimpleProducerConsumer

touch main.go

go get -u github.com/apache/pulsar-client-go
