import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HeaderComponent} from './components/header/header.component';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {FooterComponent} from './components/footer/footer.component';
import {FlexLayoutModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {MaterialModule} from '../material/material.module';


@NgModule({
    declarations: [
        HeaderComponent,
        SidebarComponent,
        FooterComponent
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        RouterModule,
        MaterialModule
    ],
    exports: [
        HeaderComponent,
        SidebarComponent,
        FooterComponent,
        MaterialModule
    ]
})
export class SharedModule {
}
