# Create a new config named "astra-streaming-examples" that is connected to an astra org
astra config create \
    "astra-streaming-examples" \
    --token "$ASTRA_TOKEN"

# Optionally set the org as default
#astra config use "astra-streaming-examples"