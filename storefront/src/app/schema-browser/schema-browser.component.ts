import {Component, OnInit} from '@angular/core';
import {SchemaService} from '../schema.service';

@Component({
    selector: 'app-schema-browser',
    templateUrl: './schema-browser.component.html',
    styleUrls: ['./schema-browser.component.scss']
})
export class SchemaBrowserComponent implements OnInit {
    schema: string;

    constructor(private schemaService: SchemaService) {
    }

    ngOnInit() {
      this.schema = this.schemaService.getSchema();
    }

}
