import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, retry} from 'rxjs/operators';
import {Router} from "@angular/router";
import {ErrorResponse} from "../dto/errorResponse";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

    constructor(private router: Router) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request)
            .pipe(
                retry(0),
                catchError((error: HttpErrorResponse) => {
                    let errorResponse: ErrorResponse = error.error;
                    if (error.error instanceof ErrorEvent) {
                        errorResponse.message = 'Client error: ' + errorResponse.message;
                    } else {
                        this.processErrorMessage(errorResponse);
                    }

                    return throwError(errorResponse.message);
                })
            );
    }

    private processErrorMessage(error: ErrorResponse) {
        if (error.status === 403) {
            error.message = 'Authentication Failure. Please log in before any updates.';
            this.router.navigate(['login']);
        } else {
            error.message = 'Error: ' + error.status + ' ' + error.error + ', path: ' + error.path;
        }
    }
}
