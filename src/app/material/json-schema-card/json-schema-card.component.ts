import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {StoreRoomService} from '../../service/storeroom/store-room.service';

@Component({
    selector: 'app-json-schema-card',
    templateUrl: './json-schema-card.component.html',
    styleUrls: ['./json-schema-card.component.scss']
})
export class JsonSchemaCardComponent implements OnInit {

    @Input() schema: any;
    public schemaVersions: any[];
    public totalElements: number;
    panelOpenState = false;

    constructor(private router: Router, private storeroomClient: StoreRoomService) {
    }

    ngOnInit(): void {
    }

    onExpansionPanel(): void {
        this.storeroomClient.getSchemaVersionList(this.schema.accession).subscribe((page) => {
            this.totalElements = page.page.totalElements;
            this.schemaVersions = page._embedded.schemas;
        });
    }
}
