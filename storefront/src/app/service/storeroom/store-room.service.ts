import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Page} from '../../dto/dto.module';

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

    public getSchemaBlockPages(schemaBlocksPages: Page, pageNumber?: number, pageSize?: number): void {
        let url = 'http://localhost:8080/api/v1/schemas/page';
        url = pageNumber ? url + '?page=' + pageNumber : url;
        url = pageSize ? url + '?size=' + pageSize : url;

        this.http.get<any>(url)
            .subscribe((response: HttpResponse<Page>) => {
                if (200 === response.status) {
                    schemaBlocksPages = response.body;
                }
            });
    }
}
