import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class StoreRoomService {
    constructor(private http: HttpClient) {
    }

    public createJsonSchema(jsonSchema: object): void {
        this.http.post('http://localhost:8080/api/v1/schemas', jsonSchema)
            .subscribe((response) => {
                console.log(response);
            });
    }

    public getJsonSchemas(pageNumber: number, pageSize: number): void {
        let url = 'http://localhost:8080/api/v1//schemas/page';
        url = pageNumber ? url + '?page=' + pageNumber : url;
        url = pageSize ? url + '?size=' + pageSize : url;

        this.http.get(url)
            .subscribe((response) => {
                console.log(response);
            });
    }
}
