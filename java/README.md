# Hightower API Java Examples

## Getting Started

* Install the JDK and [Maven](https://maven.apache.org/install.html)
* Update `src/main/java/com/gethightower/Configuration.java` with your API credentials.
* Run `mvn -q compile`

## Creating Asset Attachment

Run the following (with a valid `asset_id` for your account.)

```sh
asset_id=1234
mvn -q compile exec:java \
	-Dexec.mainClass="com.gethightower.CreateAssetAttachment" \
	-Dexec.args="$asset_id MARKETING_FLYER src/main/resources/flyer.pdf"
```

## Creating Space Attachment With Client IDs

```sh
client_asset_id=1234
client_space_id=5678
mvn -q compile exec:java \
	-Dexec.mainClass="com.gethightower.CreateSpaceAttachmentWithClientIds" \
	-Dexec.args="$client_asset_id $client_space_id FLOORPLAN src/main/resources/flyer.pdf"
```
