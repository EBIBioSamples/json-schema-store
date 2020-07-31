import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AppComponent} from './app.component';
import {LayoutComponent} from './layout/layout.component';
import {ContentComponent} from './layout/content/content.component';
import {SchemaListComponent} from './schema-list/schema-list.component';
import {EditorComponent} from './editor/editor.component';


const routes: Routes = [
    {path: '', redirectTo: '/home', pathMatch: 'full'},
    {
        path: 'home', component: LayoutComponent,
        children: [
            {path: '', redirectTo: 'schema', pathMatch: 'full'},
            {path: 'schema', component: SchemaListComponent},
            {path: 'editor', component: EditorComponent}
        ]
    },
    {path: '**', redirectTo: '/home', pathMatch: 'full'},
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
