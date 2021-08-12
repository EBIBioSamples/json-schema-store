export const META_SCHEMA = {
    "$schema": "http://json-schema.org/draft-07/schema#",
    "$id": "http://json-schema.org/draft-07/schema/metaschema",
    "title": "Core schema meta-schema",
    "definitions": {
        "schemaArray": {
            "type": "array",
            "minItems": 1,
            "items": {"$ref": "#"},
            "$async": true
        },
        "nonNegativeInteger": {
            "type": "integer",
            "minimum": 0,
            "$async": true
        },
        "nonNegativeIntegerDefault0": {
            "allOf": [
                {"$ref": "#/definitions/nonNegativeInteger"},
                {"default": 0}
            ],
            "$async": true
        },
        "simpleTypes": {
            "enum": [
                "array",
                "boolean",
                "integer",
                "null",
                "number",
                "object",
                "string"
            ],
            "$async": true
        },
        "stringArray": {
            "type": "array",
            "items": {"type": "string"},
            "uniqueItems": true,
            "default": [],
            "$async": true
        }
    },
    "type": ["object", "boolean"],
    "properties": {
        "$id": {
            "type": "string",
            "format": "uri-reference"
        },
        "$schema": {
            "type": "string",
            "format": "uri"
        },
        "$ref": {
            "type": "string",
            "format": "uri-reference"
        },
        "$comment": {
            "type": "string"
        },
        "title": {
            "type": "string"
        },
        "description": {
            "type": "string"
        },
        "deprecationMessage": {
            "type": "string",
            "description": "Non-standard: deprecation message for a property, if it is deprecated"
        },
        "x-intellij-html-description": {
            "type": "string",
            "description": "Description in html format"
        },
        "x-intellij-language-injection": {
            "type": "string",
            "description": "IntelliJ language ID for language injection"
        },
        "x-intellij-case-insensitive": {
            "type": "boolean",
            "description": "If 'true', enum options for this value will be checked case-insensitively"
        },
        "default": true,
        "readOnly": {
            "type": "boolean",
            "default": false
        },
        "examples": {
            "type": "array",
            "items": true
        },
        "multipleOf": {
            "type": "number",
            "exclusiveMinimum": 0
        },
        "maximum": {
            "type": "number"
        },
        "exclusiveMaximum": {
            "type": "number"
        },
        "minimum": {
            "type": "number"
        },
        "exclusiveMinimum": {
            "type": "number"
        },
        "maxLength": {"$ref": "#/definitions/nonNegativeInteger"},
        "minLength": {"$ref": "#/definitions/nonNegativeIntegerDefault0"},
        "pattern": {
            "type": "string",
            "format": "regex"
        },
        "additionalItems": {"$ref": "#"},
        "items": {
            "anyOf": [
                {"$ref": "#"},
                {"$ref": "#/definitions/schemaArray"}
            ],
            "default": true
        },
        "maxItems": {"$ref": "#/definitions/nonNegativeInteger"},
        "minItems": {"$ref": "#/definitions/nonNegativeIntegerDefault0"},
        "uniqueItems": {
            "type": "boolean",
            "default": false
        },
        "contains": {"$ref": "#"},
        "maxProperties": {"$ref": "#/definitions/nonNegativeInteger"},
        "minProperties": {"$ref": "#/definitions/nonNegativeIntegerDefault0"},
        "required": {"$ref": "#/definitions/stringArray"},
        "additionalProperties": {"$ref": "#"},
        "definitions": {
            "type": "object",
            "additionalProperties": {"$ref": "#"},
            "default": {}
        },
        "properties": {
            "type": "object",
            "additionalProperties": {"$ref": "#"},
            "default": {}
        },
        "patternProperties": {
            "type": "object",
            "additionalProperties": {"$ref": "#"},
            "propertyNames": {"format": "regex"},
            "default": {}
        },
        "dependencies": {
            "type": "object",
            "additionalProperties": {
                "anyOf": [
                    {"$ref": "#"},
                    {"$ref": "#/definitions/stringArray"}
                ]
            }
        },
        "propertyNames": {"$ref": "#"},
        "const": true,
        "enum": {
            "type": "array",
            "items": true,
            "minItems": 1,
            "uniqueItems": true
        },
        "type": {
            "anyOf": [
                {"$ref": "#/definitions/simpleTypes"},
                {
                    "type": "array",
                    "items": {"$ref": "#/definitions/simpleTypes"},
                    "minItems": 1,
                    "uniqueItems": true
                }
            ]
        },
        "format": {"type": "string"},
        "contentMediaType": {"type": "string"},
        "contentEncoding": {"type": "string"},
        "if": {"$ref": "#"},
        "then": {"$ref": "#"},
        "else": {"$ref": "#"},
        "allOf": {"$ref": "#/definitions/schemaArray"},
        "anyOf": {"$ref": "#/definitions/schemaArray"},
        "oneOf": {"$ref": "#/definitions/schemaArray"},
        "not": {"$ref": "#"}
    },
    "default": true,
    "$async": true
};

export const INIT_SCHEMA = {
    $schema: 'http://json-schema.org/draft-07/schema#',
    $id: 'https://www.ebi.ac.uk/biosamples/schemas/my-test-schema/1.0.0',
    title: 'title',
    description: 'description',
    type: 'object',
    meta: {
        contributors: [{description: 'X-Files Working Group'}, {description: 'Dana Scully', id: 'orcid:xxxx'}],
        provenance: [{description: 'description', id: 'https://github.com/xfiles/xfiles/xxxxxx'}],
        used_by: [{description: 'Phenopackets', id: 'https://github.com/xfiles/xfiles/xxxx'}],
        sb_status: 'implemented'
    },
    properties: {
        term: {
            allof: [{$ref: 'https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json'}, {
                description: 'The identifier of this disease\ne.g. MONDO:0007043, OMIM:101600, Orphanet:710, DOID:14705 (note these are all equivalent)\n',
                examples: [{id: 'MONDO:0007043'}]
            }]
        }
    },
    required: ['term'],
    additionalProperties: false,
    examples: [{term: {id: 'MONDO:0007043', label: 'Pfeiffer syndrome'}}],
    $async: true
};