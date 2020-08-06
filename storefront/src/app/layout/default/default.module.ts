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


@NgModule({
    declarations: [
        DefaultComponent,
        HomeComponent,
        EditorComponent,
        SchemaListComponent
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        RouterModule,
        SharedModule,
        NgJsonEditorModule,
        MaterialModule
    ]
})
export class DefaultModule {
}
