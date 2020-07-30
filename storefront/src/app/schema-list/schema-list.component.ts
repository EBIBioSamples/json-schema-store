import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../service/storeroom/store-room.service';
import {Page} from '../dto/dto.module';
import {Observable} from 'rxjs';
import {async} from 'rxjs/internal/scheduler/async';

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

    public schemaBlocksPages: Observable<Page>;
    public pageSliceSchema: any[];
    public totalElements: number;

    constructor(private storeroomClient: StoreRoomService) {
    }

    ngOnInit(): void {
        this.getSchemaBlockPages();
        this.schemaBlocksPages.subscribe(page => {
            this.pageSliceSchema = page.content.slice(0, 3);
            this.totalElements = page.totalElements;
        });
    }

    getSchemaBlockPages(): void {
        this.schemaBlocksPages = this.storeroomClient.getSchemaBlockPages2();
        console.log('page object: ');
        console.log(this.schemaBlocksPages);
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
        this.schemaBlocksPages = this.storeroomClient.getSchemaBlockPages2(event.pageIndex, event.pageSize);
        this.schemaBlocksPages.subscribe((page) => {
            if (endIndex > page.totalElements) {
                endIndex = page.totalElements;
            }
            this.pageSliceSchema = page.content.slice(startIndex, endIndex);
        });
    }

}
