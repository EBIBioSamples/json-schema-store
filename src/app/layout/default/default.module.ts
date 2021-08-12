import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DefaultComponent} from './default.component';

import {FlexLayoutModule} from '@angular/flex-layout';
import {HomeComponent} from '../../modules/home/home.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterModule} from '@angular/router';
import {EditorComponent} from '../../modules/editor/editor.component';
import {NgJsonEditorModule} from 'ang-jsoneditor';
import {SchemaListComponent} from '../../modules/schema-list/schema-list.component';
import {MaterialModule} from '../../material/material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule} from "@angular/forms";


@NgModule({
    declarations: [
        DefaultComponent,
        HomeComponent,
        EditorComponent,
        SchemaListComponent
    ],
    imports: [
        BrowserAnimationsModule,
        CommonModule,
        FlexLayoutModule,
        RouterModule,
        SharedModule,
        NgJsonEditorModule,
        MaterialModule,
        FormsModule
    ]
})
export class DefaultModule {
}
