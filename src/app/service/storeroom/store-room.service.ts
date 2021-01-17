import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Page} from '../../dto/dto.module';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {ValidationResponse} from '../../dto/validationResponse';

@Injectable({
    providedIn: 'root'
})
export class StoreRoomService {
    public page: Page;
    private storeroomApi = environment.storeroom_api;

    constructor(private http: HttpClient) {
    }

    // Get Operations
    public getMetaSchema(): Observable<any> {
        return this.http.get(this.storeroomApi + '/metaSchemas');
    }

    public getAllMetaSchema(): Observable<any> {
        return this.http.get(this.storeroomApi + '/metaSchemas');
    }

    public getSchemaBlockPages(pageNumber = 0, pageSize = 3): Observable<Page> {
        const url = this.storeroomApi + '/schemas?page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<Page>(url);
    }

    public getSchemaBlockById(id: string): Observable<any> {
        const url = this.storeroomApi + '/schemas/id?id=' + id;
        return this.http.get(url);
    }

    public getSchemaVersionList(id: string, pageNumber = 0, pageSize = 3): Observable<any> {
        const schemaName = id.substring(0, id.lastIndexOf('/'));
        const url = this.storeroomApi + '/schemas/versions?schemaName=' + schemaName + '&page=' + pageNumber + '&size=' + pageSize;
        return this.http.get(url);
    }

    public searchSchemas(searchKey = '', pageNumber = 0, pageSize = 3): Observable<any> {
        const url = this.storeroomApi + '/schemas?text=' + searchKey + '&page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<Page>(url);
    }

    // Post Operations

    public createJsonSchema(jsonSchema: object): Observable<any> {
        return this.http.post(this.storeroomApi + '/schemas', jsonSchema);
    }

    public validateSchema(jsonSchema: object): Observable<any> {
        return this.http.post<ValidationResponse>(this.storeroomApi + '/validate', jsonSchema);
    }

    // Put Operations

    public updateSchemaBlock(jsonSchema: object): Observable<any> {
        console.log(jsonSchema);
        console.log(jsonSchema['schema']);
        return this.http.put(this.storeroomApi + '/schemas?id=' + jsonSchema['schema']['$id'] , jsonSchema);
    }

    // Delete Operations

    public deleteSchemaBlock(id: string): Observable<any> {
        return this.http.delete(this.storeroomApi + '/schemas/?id=' + id);
    }
}
