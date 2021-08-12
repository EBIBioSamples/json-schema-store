import {Injectable} from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from "../service/auth.service";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    constructor(private auth: AuthService) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        if (this.auth.isLoggedIn()) {
            request = request.clone({
                setHeaders: {
                    'Authorization': 'Basic ' + btoa(this.auth.getToken())
                    // Authorization: `Bearer ${this.auth.getToken()}`
                }
            });
        }

        return next.handle(request);
    }
}
