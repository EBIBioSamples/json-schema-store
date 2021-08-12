import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-schema-list',
    templateUrl: './schema-list.component.html',
    styleUrls: ['./schema-list.component.scss']
})
export class SchemaListComponent implements OnInit {
    public totalElements: number;
    public pageSize: number;
    public pageIndex: number;
    public pageSizeOptions: number[];
    public schemas: object[];
    public searchText: string;

    constructor(private storeroomClient: StoreRoomService, private router: Router) {
    }

    ngOnInit(): void {
        this.searchText = '';
        this.pageSize = 5;
        this.pageIndex = 0;
        this.pageSizeOptions = [5, 10, 20]

        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.search();
    }

    search(): void {
        this.getSchema(this.searchText, this.pageSize, this.pageIndex);
    }

    onPageChange(event: PageEvent): void {
        this.getSchema(this.searchText, event.pageSize, event.pageIndex);
    }

    getSchema(text: string, pageSize: number, pageIndex: number) {
        this.storeroomClient.searchSchema(text, pageIndex, pageSize).subscribe((schemaPage) => {
            this.pageIndex = schemaPage.page.number;
            this.pageSize = schemaPage.page.size;
            this.totalElements = schemaPage.page.totalElements;
            this.schemas = schemaPage._embedded.schemas;
        })
    }
}
