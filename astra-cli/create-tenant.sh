# Create a tenant within the "astra-streaming-examples" astra org
astra streaming create \
  --config "astra-streaming-examples" \
  --cloud "$CLOUD_PROVIDER_STREAMING" \
  --region "$CLOUD_REGION_STREAMING" \
  "$TENANT"