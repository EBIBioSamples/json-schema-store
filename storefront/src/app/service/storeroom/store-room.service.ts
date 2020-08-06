import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Page} from '../../dto/dto.module';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class StoreRoomService {
    public page: Page;

    constructor(private http: HttpClient) {
    }

    public createJsonSchema(jsonSchema: object): void {
        this.http.post('http://localhost:8080/api/v1/schemas', jsonSchema)
            .subscribe((response) => {
                console.log(response);
            });
    }

    public getSchemaBlockPages(pageNumber = 0, pageSize = 3): Observable<Page> {
        const url = 'http://localhost:8080/api/v1/schemas/page?page=' + pageNumber + '&size=' + pageSize;
        return this.http.get<Page>(url);
    }

    public getSchemaBlockById(id: string): Observable<any> {
        const url = 'http://localhost:8080/api/v1/schemas/?id=' + id;
        return this.http.get(url);
    }

    public updateSchemaBlock(jsonSchema: object): void {
        this.http.put('http://localhost:8080/api/v1/schemas', jsonSchema)
            .subscribe((response) => {
                console.log(response);
            });
    }
}
