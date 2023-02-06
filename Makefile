compile:
	@mvn package -DskipTests 
	@echo "Done."

install:
	@mvn clean install -DskipTests
	@echo "Done."

oar:
	@find . -name '*.oar'

generate:
	@mvn archetype:generate -DarchetypeGroupId=org.onosproject -DarchetypeArtifactId=onos-bundle-archetype -DarchetypeVersion=2.0.0
	@echo "Done."