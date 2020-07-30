import {NgModule} from '@angular/core';
import { MatRippleModule } from '@angular/material/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';

import {JsonSchemaCardComponent} from './json-schema-card/json-schema-card.component';

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
    MatPaginatorModule
];

@NgModule({
    imports: [material],
    declarations: [
        JsonSchemaCardComponent
    ],
    exports: [material, JsonSchemaCardComponent]
})
export class MaterialModule {}