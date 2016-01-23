
Getting Started
===============

* Install the JDK and (Maven)[https://maven.apache.org/install.html]
* Update `src/main/java/com/gethightower/api/examples/Configuration.java` with your API credentials.
* Run `mvn -q compile`

Uploading Attachment
====================

Run the following (changing `asset_id` to a valid asset ID in your environment.)

```sh
asset_id=1234
mvn -q compile exec:java \
	-Dexec.mainClass="com.gethightower.api.examples.UploadFile" \
	-Dexec.args="$asset_id MARKETING_FLYER src/main/resources/flyer.pdf"
```
