import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {SchemaPage} from '../../dto/dto.module';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';

@Component({
    selector: 'app-schema-list',
    templateUrl: './schema-list.component.html',
    styleUrls: ['./schema-list.component.scss']
})
export class SchemaListComponent implements OnInit {

    public schemaBlocks: any[];
    public totalElements: number;
    public search: any;
    private schemaBlocksPages: Observable<SchemaPage>;
    private isSearchMode = false;

    constructor(private storeroomClient: StoreRoomService, private router: Router) {
    }

    ngOnInit(): void {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.getSchemaBlockPages();
        this.schemaBlocksPages.subscribe(page => {
            this.totalElements = page.page.totalElements;
            this.schemaBlocks = page._embedded.schemas;
        });
    }

    getSchemaBlockPages(): void {
        this.schemaBlocksPages = this.storeroomClient.getJsonSchemaPage();
    }

    onPageChange(event: PageEvent): void {
        if (this.isSearchMode) {
            this.schemaBlocksPages = this.storeroomClient.searchSchema(this.search.value, event.pageIndex, event.pageSize);
        } else {
            this.schemaBlocksPages = this.storeroomClient.getJsonSchemaPage(event.pageIndex, event.pageSize);
        }
        this.schemaBlocksPages.subscribe((page) => {
            this.totalElements = page.page.totalElements;
            this.schemaBlocks = page._embedded.schemas;
        });
    }

    doSearch(searchKey: any): void {
        this.isSearchMode = true;
        this.search = searchKey;
        this.schemaBlocksPages = this.storeroomClient.searchSchema(searchKey.value);
        this.schemaBlocksPages.subscribe((page) => {
            this.totalElements = page.page.totalElements;
            this.schemaBlocks = page._embedded.schemas;
        });
    }

}
