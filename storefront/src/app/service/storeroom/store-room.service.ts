import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
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

    public getMetaSchema(): Observable<any> {
        return this.http.get(this.storeroomApi + '/metaSchemas');
    }

    public createJsonSchema(jsonSchema: object): Observable<any> {
        return this.http.post(this.storeroomApi + '/schemas', jsonSchema);
    }

    public getSchemaBlockPages(pageNumber = 0, pageSize = 3): Observable<Page> {
        const url = this.storeroomApi + '/schemas/page?page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<Page>(url);
    }

    public getSchemaBlockById(id: string): Observable<any> {
        const url = this.storeroomApi + '/schemas/?id=' + id;
        return this.http.get(url);
    }

    public updateSchemaBlock(jsonSchema: object): Observable<any> {
        return this.http.put(this.storeroomApi + '/schemas', jsonSchema);
    }

    public deleteSchemaBlock(id: string): Observable<any> {
        return this.http.delete(this.storeroomApi + '/schemas/?id=' + id);
    }

    public validateSchema(jsonSchema: object): Observable<any> {
        return this.http.post<ValidationResponse>(this.storeroomApi + '/validate', jsonSchema);
    }
}
