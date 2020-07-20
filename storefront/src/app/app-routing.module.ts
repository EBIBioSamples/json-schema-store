import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SchemaBrowserComponent} from './schema-browser/schema-browser.component';
import {DashboardComponent} from './dashboard/dashboard.component';


const routes: Routes = [
    {path: '', redirectTo: '/dashboard', pathMatch: 'full'},
    {path: 'dashboard', component: DashboardComponent},
    {path: 'browse', component: SchemaBrowserComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
