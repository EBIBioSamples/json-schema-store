import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private schemaStoreApi = environment.schema_store_api;
    private loggedIn: boolean

    // todo implement token based auth
    private username: string;
    private password: string;
    private token: string;

    constructor(private http: HttpClient, private router: Router) {
        this.loggedIn = false;
    }

    public isLoggedIn() {
        return this.loggedIn;
    }

    public getToken(): string {
        return this.token;
    }

    public login(username: string, password: string): Observable<boolean> {
        const loginRequest = {'username': username, 'password': password};
        this.http.post(this.schemaStoreApi + '/auth/login', loginRequest).subscribe((response) => {
            if (response["success"]) {
                this.loggedIn = true;
                this.username = username;
                this.password = password;
                this.token = response["token"];

                //todo move this to component
                this.router.navigate(['schemas'])
            } else {
                console.log(response["error"])
                this.loggedIn = false;
            }
        }, (error) => {
            console.log("Failed to log in.")
            this.loggedIn = false;
        });

        return of(this.loggedIn);
    }

    public logout(): void {
        this.loggedIn = false;
        this.username = null;
        this.password = null;
        this.token = null;

        this.http.post(this.schemaStoreApi + '/logout', null).subscribe((response) => {
            console.log("Logged out successfully.");
        }, (error) => {
            console.log("Failed to log out.");
        });
    }

}
