compile:
	@mvn compile 
	@echo "Done."

install:
	@mvn install
	@echo "Done."

oar:
	@find . -name '*.oar'

generate:
	@mvn archetype:generate
	@echo "Done."