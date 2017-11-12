# simple-java-json-parser

[![Build Status](https://travis-ci.org/andyglow/simple-java-json-parser.svg?branch=master)](https://travis-ci.org/andyglow/simple-java-json-parser)
[![Coverage Status](https://coveralls.io/repos/github/andyglow/simple-java-json-parser/badge.svg?branch=master)](https://coveralls.io/github/andyglow/simple-java-json-parser?branch=master)

Simple Java JSON Parser

Remember SAX for XML parsing?
This project aims to implement the same idea but for JSON parsing.

The second goal is to provide lightweight embeddable JSON parser which would be used by code copying (not depending on this project).

# API 

API is simple.

We have Handler which accepts events of type:
- START
- END
- OBJECT_START
- OBJECT_END
- ARRAY_START
- ARRAY_END
- NAME
- VALUE

And we have Parser which takes a stream of bytes (or string) and generates sequence of events.
Which is basically it. All the code is consists in one file/class.

## Example

```
import json.Parser
import static json.Parser.*;

Handler handler = ...
new Parser("{ \"foo\": \"bar\" }", handler).parse();

```