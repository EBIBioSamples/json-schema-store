import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-json-schema-card',
  templateUrl: './json-schema-card.component.html',
  styleUrls: ['./json-schema-card.component.scss']
})
export class JsonSchemaCardComponent implements OnInit {

  // @Input() cardNumber: number;
  @Input() schemaBlock: object;
  constructor() { }

  ngOnInit(): void {
  }

}
