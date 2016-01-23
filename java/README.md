
Getting Started
===============

* Install Java and Maven
* Update the API credentials in `src/main/java/com/gethightower/api/examples/Configuration.java`
* Run `mvn -q compile`

Uploading Attachment
====================

Run the following, setting asset_id to a valid asset ID in your environment.

```sh
asset_id=1234
mvn -q compile exec:java -Dexec.mainClass="com.gethightower.api.examples.UploadFile" -Dexec.args="$asset_id MARKETING_FLYER src/main/resources/flyer.pdf"
```
