# tag::prep-env[]
wget https://archive.apache.org/dist/pulsar/pulsar-2.10.2/DEB/apache-pulsar-client.deb
wget https://archive.apache.org/dist/pulsar/pulsar-2.10.2/DEB/apache-pulsar-client-dev.deb

sudo apt install -y ./apache-pulsar-client.deb
sudo apt install -y ./apache-pulsar-client*.deb

sudo ldconfig
# end::prep-env[]

# tag::create-project[]
mkdir SimpleProducerConsumer

cd SimpleProducerConsumer

npm init

# Let npm use default values
#   package name: (simpleproducerconsumer)
#   version: (1.0.0)
#   description:
#   entry point: (index.js)
#   test command:
#   git repository:
#   keywords:
#   author:
#   license: (ISC)

npm install pulsar-client

touch index.js
# end::create-project[]