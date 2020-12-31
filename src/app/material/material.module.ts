import {NgModule} from '@angular/core';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MatRippleModule} from '@angular/material/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatCardModule} from '@angular/material/card';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatSnackBarModule} from '@angular/material/snack-bar';

import {JsonSchemaCardComponent} from './json-schema-card/json-schema-card.component';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';

const material = [
    MatSidenavModule,
    MatRippleModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatListModule,
    MatPaginatorModule,
    MatExpansionModule,
    MatSnackBarModule
];

@NgModule({
    imports: [material, RouterModule, CommonModule, FlexLayoutModule],
    declarations: [
        JsonSchemaCardComponent
    ],
    exports: [material, JsonSchemaCardComponent]
})
export class MaterialModule {
}
