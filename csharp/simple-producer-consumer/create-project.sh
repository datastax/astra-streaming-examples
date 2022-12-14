dotnet new console \
  --output SimpleProducerConsumer \
  --framework net7.0

cd SimpleProducerConsumer

dotnet add package DotPulsar --version 2.7.0