import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class SchemaService {

    constructor() {
    }

    getSchema(): string {
        return '{}';
    }
}
