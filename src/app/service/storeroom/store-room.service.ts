import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SchemaPage} from '../../dto/dto.module';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {ValidationResponse} from '../../dto/validationResponse';

@Injectable({
    providedIn: 'root'
})
export class StoreRoomService {
    public page: SchemaPage;
    private storeroomApi = environment.storeroom_api;

    constructor(private http: HttpClient) {
    }

    // Get Operations
    public getMetaSchema(): Observable<any> {
        return this.http.get(this.storeroomApi + '/metaSchemas');
    }

    public getAllMetaSchema(): Observable<any> {
        return this.http.get(this.storeroomApi + '/metaSchemas/search');
    }

    public getJsonSchemaPage(pageNumber = 0, pageSize = 3): Observable<SchemaPage> {
        const url = this.storeroomApi + '/schemas/search?page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<SchemaPage>(url);
    }

    public getSchema(id: string): Observable<any> {
        const url = this.storeroomApi + '/schemas?id=' + id;
        return this.http.get(url);
    }

    public getSchemaVersionList(id: string, pageNumber = 0, pageSize = 3): Observable<any> {
        const schemaName = id.substring(0, id.lastIndexOf('/'));
        const url = this.storeroomApi + '/schemas/versions?schemaName=' + schemaName + '&page=' + pageNumber + '&size=' + pageSize;
        return this.http.get(url);
    }

    public searchSchema(searchKey = '', pageNumber = 0, pageSize = 3): Observable<any> {
        const url = this.storeroomApi + '/schemas/search?text=' + searchKey + '&page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<SchemaPage>(url);
    }

    public createSchema(jsonSchema: object): Observable<any> {
        return this.http.post(this.storeroomApi + '/schemas', jsonSchema);
    }

    public validateSchema(jsonSchema: object): Observable<any> {
        return this.http.post<ValidationResponse>(this.storeroomApi + '/validate', jsonSchema);
    }

    public updateSchema(jsonSchema: object): Observable<any> {
        return this.http.put(this.storeroomApi + '/schemas?id=' + jsonSchema['schema']['$id'] , jsonSchema);
    }

    public deleteSchema(id: string): Observable<any> {
        return this.http.delete(this.storeroomApi + '/schemas/?id=' + id);
    }
}
