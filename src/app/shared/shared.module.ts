import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HeaderComponent} from './components/header/header.component';
import {FlexLayoutModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {MaterialModule} from '../material/material.module';


@NgModule({
    declarations: [
        HeaderComponent,
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        RouterModule,
        MaterialModule
    ],
    exports: [
        HeaderComponent,
        MaterialModule
    ]
})
export class SharedModule {
}
