import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DefaultComponent} from './layout/default/default.component';
import {HomeComponent} from './modules/home/home.component';
import {EditorComponent} from './modules/editor/editor.component';
import {SchemaListComponent} from './modules/schema-list/schema-list.component';
import {AboutComponent} from "./modules/about/about.component";
import {LoginComponent} from "./modules/login/login.component";

const routes: Routes = [
    {path: '', component: DefaultComponent,
    children: [
        {path: '', component: HomeComponent},
        {path: 'editor', component: EditorComponent},
        {path: 'schemas', component: SchemaListComponent},
        {path: 'about', component: AboutComponent},
        {path: 'login', component: LoginComponent}
    ]}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
