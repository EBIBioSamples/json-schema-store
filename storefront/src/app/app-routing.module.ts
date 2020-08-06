import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DefaultComponent} from './layout/default/default.component';
import {HomeComponent} from './modules/home/home.component';
import {EditorComponent} from './modules/editor/editor.component';
import {SchemaListComponent} from './modules/schema-list/schema-list.component';

const routes: Routes = [
    {path: '', component: DefaultComponent,
    children: [
        {path: '', component: HomeComponent},
        {path: 'editor', component: EditorComponent},
        {path: 'schemas', component: SchemaListComponent}
    ]}
];

/*const routes: Routes = [
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
];*/

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
