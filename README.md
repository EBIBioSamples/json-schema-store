[![](https://img.shields.io/badge/EBIBioSamples-json--schema--repo-blue)](https://www.ebi.ac.uk/)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://img.shields.io/badge/spring--boot-2.2.7.RELEASE-green)]()
# json-schema-store
A repository for storing versioned JSON Schema

## Project Description
JSON Schema is a vocabulary to specify the structure of a JSON document. JSON Schema is widely used and well supported and sees a growing use for many GA4GH standard representation e.g. through the “SchemaBlocks” project.
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

## Table of Content
- [Software](#software)
- [Installation](#installation)
- [Schema UI (storefront)](#schema-ui-storefront)
- [Documentations](#documentations)

### Software
* Java OpenJdk-11
* Apache Maven 3.6.3
* Docker 19.03.8
* docker-compose:1.25.5
* MongoDB 4.2.6
### Installation
Run this in terminal to run the application.

#### Option 1
```shell script
$ cd json-schema-store
$ mvn clean install -DskipTests
$ cd ../storeroom
$ docker-compose up --build
```
#### Option 2
```shell script
$ sh run.sh
```
### Schema UI (storefront)
After running the application Schema UI can be access via browser
```http request
http://localhost:8080
```
### SchemaRepo (Document Store)
To access docker container of mogodb server from the host, find the IP address and continue with that
```shell script
$ docker ps # to get the container ID
$ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container_name_or_id
```
#### example
```shell script
$ docker ps
CONTAINER ID        IMAGE                                        COMMAND                  CREATED             STATUS              PORTS                                 NAMES
76eb0212d926        biosamples/json-schema-store:latest          "java -jar /app.jar …"   4 minutes ago       Up 4 minutes        0.0.0.0:8080->8080/tcp                storeroom_app_1
cfb03a5f8125        mongo:4.2.6                                  "docker-entrypoint.s…"   4 minutes ago       Up 4 minutes        27017/tcp, 0.0.0.0:27030->27030/tcp   SchemaRepo
5ca6e7dde0c9        ebispot/elixir-jsonsschema-validator:1.3.0   "npm start"              4 minutes ago       Up 4 minutes        0.0.0.0:3000->3020/tcp                validator
$ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' cfb03a5f8125
172.18.0.3
```
### Documentations
* [API](https://github.com/EBIBioSamples/json-schema-store/wiki/API-Reference)
* [Further Improvements](https://github.com/EBIBioSamples/json-schema-store/wiki/Further-Improvements)

### Notes
Started as a [GSoC 2020](https://gist.github.com/Hashan-Rashmi-Perera/99f6983bcfb11e7990abbdd484796854) project under GA4GH 
