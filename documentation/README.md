# Building the Architecture Documentation

The documentation can be compiled into a pdf using [Pandoc](https://pandoc.org/).

```
pandoc main.md architecture.md -o documentation.pdf
```


The UML diagrams can be compiled using [plantuml](http://plantuml.com/).

```
java -jar plantuml.jar extensions.plantuml
```


This directory also contains a Makefile to perform this steps, simply run `make documentation` to compile the documentation, `make uml` to compile the UML diagrams or `make all` to do both. You'll need to place the `plantuml.jar` into this directory to compile the UML files using the Makefile.

If you're viewing this on GitLab, you can also directly read the documentation [here](architecture.md).