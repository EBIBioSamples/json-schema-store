import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';

import {AppRoutingModule} from './app-routing.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material/material.module';
import {NgJsonEditorModule} from 'ang-jsoneditor';

import {AppComponent} from './app.component';
import {StoreRoomService} from './service/storeroom/store-room.service';
import {DefaultModule} from './layout/default/default.module';
import {HttpErrorInterceptor} from './interceptors/http-error.interceptor';
import { AboutComponent } from './modules/about/about.component';
import { LoginComponent } from './modules/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { ChecklistEditorComponent } from './modules/checklist-editor/checklist-editor.component';

@NgModule({
    declarations: [
        AppComponent,
        AboutComponent,
        LoginComponent,
        ChecklistEditorComponent
    ],
    imports: [
        BrowserModule,
        FlexLayoutModule,
        BrowserAnimationsModule,
        MaterialModule,
        AppRoutingModule,
        NgJsonEditorModule,
        HttpClientModule,
        DefaultModule,
        FormsModule,
        ReactiveFormsModule
    ],
    providers: [
        StoreRoomService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpErrorInterceptor,
            multi: true
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
