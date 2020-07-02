import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemaBrowserComponent } from './schema-browser.component';

describe('SchemaBrowserComponent', () => {
  let component: SchemaBrowserComponent;
  let fixture: ComponentFixture<SchemaBrowserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchemaBrowserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemaBrowserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
