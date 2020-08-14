import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    selector: 'app-json-schema-card',
    templateUrl: './json-schema-card.component.html',
    styleUrls: ['./json-schema-card.component.scss']
})
export class JsonSchemaCardComponent implements OnInit {

    @Input() schemaBlock: any;
    panelOpenState = false;

    constructor(private router: Router) {
    }

    ngOnInit(): void {
    }

}
