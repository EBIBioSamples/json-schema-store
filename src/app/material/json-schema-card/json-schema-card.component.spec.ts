import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JsonSchemaCardComponent } from './json-schema-card.component';

describe('JsonSchemaCardComponent', () => {
  let component: JsonSchemaCardComponent;
  let fixture: ComponentFixture<JsonSchemaCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JsonSchemaCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JsonSchemaCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
