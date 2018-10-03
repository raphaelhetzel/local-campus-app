all: uml documentation

uml:
	java -jar plantuml.jar dataflow.plantuml
	java -jar plantuml.jar messages.plantuml
	java -jar plantuml.jar extensions.plantuml
	java -jar plantuml.jar FrontendArchitecture.plantuml
	
documentation:
	pandoc main.md architecture.md -o documentation.pdf