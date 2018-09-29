MarkdownSources=main.md architecture.md

all: documentation.pdf
	
documentation.pdf: ${MarkdownSources}
	pandoc ${MarkdownSources} -o $@