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
}
