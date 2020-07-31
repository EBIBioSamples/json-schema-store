import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../service/storeroom/store-room.service';
import {Page} from '../dto/dto.module';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-schema-list',
    templateUrl: './schema-list.component.html',
    styleUrls: ['./schema-list.component.scss']
})
export class SchemaListComponent implements OnInit {

    public schemaBlocks: any[];
    public totalElements: number;
    private schemaBlocksPages: Observable<Page>;

    constructor(private storeroomClient: StoreRoomService) {
    }

    ngOnInit(): void {
        this.getSchemaBlockPages();
        this.schemaBlocksPages.subscribe(page => {
            this.schemaBlocks = page.content;
            this.totalElements = page.totalElements;
        });
    }

    getSchemaBlockPages(): void {
        this.schemaBlocksPages = this.storeroomClient.getSchemaBlockPages();
    }

    onPageChange(event: PageEvent): void {
        this.schemaBlocksPages = this.storeroomClient.getSchemaBlockPages(event.pageIndex, event.pageSize);
        this.schemaBlocksPages.subscribe((page) => {
            this.totalElements = page.totalElements;
            this.schemaBlocks = page.content;
        });
    }

}
