# json-schema-store
A repository for storing versioned JSON Schema

## Project Description
JSON Schema is a vocabulary to specify the structure of a JSON document. JSON Schema is widely used and well supported and sees a growing use for many GA4GH standard representation e.g. through the “SchemaBlocks” project.
At the moment we are missing a way to manage this growing collection of schemas. This project would like to address the following concerns:
* How can we search for specific schema? 
* How can we improve reuse of schemas that already exist?
* How to create, modify schemas? Can untrained users define schemas easily? 
* What are the best practices for schema usage and how can we encourage them?

To address the above concerns, we would like to develop a repository for storing versioned JSON Schema. The repository should have an intuitive user interface for searching, browsing, creating and modifying schema documents. 

The project will be comprised of the following tasks:
* Define a GA4GH JSON Meta-schema that can be used to validate GA4GH JSON Schemas. These schema need to be compatible with the current JSON schema validator and SchemaBlocks
* Develop a mechanism for storing and updating versioned JSON Schemas 
* Develop/adapt an editor to create and edit GA4GH schemas
* Develop a mechanism allowing submission of external JSON schemas to the repository. This should include validation of the schemas.
* From schema, generate sample data file for deposition in different formats (JSON, CSV, ..) 
* Developing a web client for browsing/searching schemas

