# https://github.com/edoardottt/offensive-onos

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

venv:
	@cd detection/log-analysis && python3 -m venv .venv && . .venv/bin/activate && python3 -m pip install -r requirements.txt