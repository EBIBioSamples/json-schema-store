export interface SchemaPage {
    _embedded: SchemaPageContent;
    _links: any;
    page: SchemaPageMetadata
}

export interface SchemaPageContent {
    schemas: any[];
}

export interface SchemaPageLinks {
    schemas: any;
}

export interface SchemaPageMetadata {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
}
