import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {PageEvent} from '@angular/material/paginator';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {Observable} from 'rxjs';
import {Page} from '../../dto/page';

@Component({
    selector: 'app-json-schema-card',
    templateUrl: './json-schema-card.component.html',
    styleUrls: ['./json-schema-card.component.scss']
})
export class JsonSchemaCardComponent implements OnInit {

    @Input() schemaBlock: any;
    public schemaBlocks: any[];
    public totalElements: number;
    panelOpenState = false;
    private schemaBlocksPages: Observable<Page>;

    constructor(private router: Router, private storeroomClient: StoreRoomService) {
    }

    ngOnInit(): void {
    }

    onExpansionPanel(): void {
        this.schemaBlocksPages = this.storeroomClient.getSchemaVersionList(this.schemaBlock.$id);
        this.schemaBlocksPages.subscribe((page) => {
            this.totalElements = page.totalElements;
            this.schemaBlocks = page.content;
        });
    }

    onPageChange($event: PageEvent): void {
        this.schemaBlocksPages = this.storeroomClient.getSchemaVersionList(this.schemaBlock.$id, $event.pageIndex, $event.pageSize);
        this.schemaBlocksPages.subscribe((page) => {
            this.totalElements = page.totalElements;
            this.schemaBlocks = page.content;
        });
    }
}
