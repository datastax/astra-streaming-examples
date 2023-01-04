# tag::prep-env[]
wget https://archive.apache.org/dist/pulsar/pulsar-2.10.2/DEB/apache-pulsar-client.deb
wget https://archive.apache.org/dist/pulsar/pulsar-2.10.2/DEB/apache-pulsar-client-dev.deb

sudo apt install -y ./apache-pulsar-client.deb
sudo apt install -y ./apache-pulsar-client*.deb

sudo ldconfig
# end::prep-env[]

# tag::create-project[]
mkdir SimpleProducerConsumer && cd SimpleProducerConsumer

touch index.js

npm init -y

npm install pulsar-client
# end::create-project[]