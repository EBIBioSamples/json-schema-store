import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {FlexLayoutModule} from '@angular/flex-layout';

import {AppRoutingModule} from './app-routing.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material/material.module';
import {NgJsonEditorModule} from 'ang-jsoneditor';

import {AppComponent} from './app.component';
import {SchemaBrowserComponent} from './schema-browser/schema-browser.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {EditorComponent} from './editor/editor.component';
import {StoreRoomService} from './service/storeroom/store-room.service';
import { LayoutComponent } from './layout/layout.component';
import { HeaderComponent } from './layout/header/header.component';
import { ContentComponent } from './layout/content/content.component';

@NgModule({
    declarations: [
        AppComponent,
        SchemaBrowserComponent,
        DashboardComponent,
        EditorComponent,
        LayoutComponent,
        HeaderComponent,
        ContentComponent
    ],
    imports: [
        BrowserModule,
        FlexLayoutModule,
        BrowserAnimationsModule,
        MaterialModule,
        AppRoutingModule,
        NgJsonEditorModule,
        HttpClientModule
    ],
    providers: [StoreRoomService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
