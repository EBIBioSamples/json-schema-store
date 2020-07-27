import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SchemaBrowserComponent} from './schema-browser/schema-browser.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {EditorComponent} from './editor/editor.component';
import {NgJsonEditorModule} from 'ang-jsoneditor';
import {StoreRoomService} from './service/storeroom/store-room.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
    declarations: [
        AppComponent,
        SchemaBrowserComponent,
        DashboardComponent,
        EditorComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        NgJsonEditorModule,
        HttpClientModule
    ],
    providers: [StoreRoomService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
