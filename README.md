[![](https://img.shields.io/badge/EBIBioSamples-json--schema--repo-blue)](https://www.ebi.ac.uk/)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://img.shields.io/badge/spring--boot-2.2.7.RELEASE-green)]()
# json-schema-store
A repository for storing versioned JSON Schema

## Table of Content
- [Software](#software)
- [Build and run](#Build and run)
- [APIs and usage](#APIs and usage)
- [Notes](#Notes)

## Software
* Java OpenJdk-11
* Apache Maven 3.6.3
* Docker 19.03.8
* docker-compose:1.25.5
* MongoDB 4.2.6

## Build and run
- Build and run with all dependencies using docker-compose
  ```shell script
  $ mvn clean install
  $ docker-compose up -d --build
  ```
- Go to `http://localhost:8085`

## APIs and usage
There were significant changes to the structure of the `schema-store` during `checklist-editor` development. The UI is no longer available, and some APIs have changed significantly. The following is a list of APIs that are used by checklist-editor and biosamples. Schema-store uses rest repositories. Therefore, most of the repository methods are available via the API. Pagnination available for list resources via `page` & `size` parameters.

- [Get checklist/schema with metadata (latest)](https://www.ebi.ac.uk/biosamples/checklist-editor/api/v2/mongoJsonSchemas/ERC000011) - `/api/v2/mongoJsonSchemas/ERC000011`
- [Get versioned schema with metadata](https://www.ebi.ac.uk/biosamples/checklist-editor/api/v2/mongoJsonSchemas/ERC000060:1.18) - `/api/v2/mongoJsonSchemas/ERC000011:1.18`
- [Get vanila JSON Schema (latest)](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/registry/schemas/ERC000011) - `/schema-store/registry/schemas/ERC000011`
- [Get vanila JSON Schema with version](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/registry/schemas/ERC000011:1.3) - `/registry/schemas/ERC000011:1.3`
- [Get schema list](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/mongoJsonSchemas) - `/api/v2/mongoJsonSchemas`
- [Get field list](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/fields) - `/api/v2/fields`
- [Search schema](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/schemas/search/findByExample?page=0&size=25&sort=name%2CASC&latest=true) - `/api/v2/schemas/search/findByExample?page=0&size=25&sort=name%2CASC&latest=true` 
- [Search schema example](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/schemas/search/findByExample?page=0&size=10&sort=lastModifiedDate%2CDESC&latest=true&authority=ENA&searchable=shellfish) - `/api/v2/schemas/search/findByExample?page=0&size=10&sort=lastModifiedDate%2CDESC&latest=true&authority=ENA&searchable=shellfish`
- [Search fields](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/fields/search/findByExample) - `/api/v2/fields/search/findByExample`
- [Schema summary list](https://www.ebi.ac.uk/biosamples/checklist-editor/schema-store/api/v2/schemas/list) - `/api/v2/schemas/list`

Rest repository exposed API examples
- `/api/v2/schemas/search/findAttributeValues?attributeName=group`
- `/api/v2/fields/search/findByUsedBySchemas?schemaId=ERC000060%3A1.18&size=300`

## Notes
Started as a [GSoC 2020](https://gist.github.com/Hashan-Rashmi-Perera/99f6983bcfb11e7990abbdd484796854) project under GA4GH

### GSoC Project Motivation
JSON Schema is a vocabulary to specify the structure of a JSON document. JSON Schema is widely used and well-supported and sees a growing use for many GA4GH standard representation e.g. through the “SchemaBlocks” project.
At the moment we are missing a way to manage this growing collection of schemas. This project would like to address the following concerns:
* How can we search for a specific schema?
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
