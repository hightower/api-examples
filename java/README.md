
Getting Started
===============

* Install the JDK and [Maven](https://maven.apache.org/install.html)
* Update `src/main/java/com/gethightower/Configuration.java` with your API credentials.
* Run `mvn -q compile`

Creating Asset Attachment
=========================

Run the following (with a valid `asset_id` for your account.)

```sh
asset_id=1234
mvn -q compile exec:java \
	-Dexec.mainClass="com.gethightower.CreateAssetAttachment" \
	-Dexec.args="$asset_id MARKETING_FLYER src/main/resources/flyer.pdf"
```
