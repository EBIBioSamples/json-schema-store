import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../service/storeroom/store-room.service';
import {Page} from '../dto/dto.module';

@Component({
    selector: 'app-schema-list',
    templateUrl: './schema-list.component.html',
    styleUrls: ['./schema-list.component.scss']
})
export class SchemaListComponent implements OnInit {

    public cardItems = [
        {cardItem: 1}, {cardItem: 2}, {cardItem: 3},
        {cardItem: 4}, {cardItem: 5}, {cardItem: 6},
        {cardItem: 7}, {cardItem: 8}, {cardItem: 9}
    ];
    public pageSlice = this.cardItems.slice(0, 3);

    public schemaBlocksPages: Page ;
    public pageSliceSchema: any[];

    constructor(private storeroomClient: StoreRoomService) {
    }

    ngOnInit(): void {
        this.getSchemaBlockPages();
        this.pageSliceSchema = this.schemaBlocksPages.content.slice(0, 3);
    }

    getSchemaBlockPages(): void {
        this.schemaBlocksPages = new Page();
        this.storeroomClient.getSchemaBlockPages(this.schemaBlocksPages);
        console.log('page object: ');
        console.log(this.schemaBlocksPages);
        // this.schemaBlocksPages;
    }

    onPageChange(event: PageEvent): void {
        console.log(event);
        const startIndex = event.pageIndex * event.pageSize;
        let endIndex = startIndex + event.pageSize;
        if (endIndex > this.cardItems.length) {
            endIndex = this.cardItems.length;
        }
        this.pageSlice = this.cardItems.slice(startIndex, endIndex);
    }

    onPageChangeSchema(event: PageEvent): void {
        console.log(event);
        const startIndex = event.pageIndex * event.pageSize;
        let endIndex = startIndex + event.pageSize;
        if (endIndex > this.schemaBlocksPages.totalElements) {
            endIndex = this.schemaBlocksPages.totalElements;
        }
        this.pageSliceSchema = this.schemaBlocksPages.content.slice(startIndex, endIndex);
    }

}
