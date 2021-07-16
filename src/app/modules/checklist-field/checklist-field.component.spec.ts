import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistFieldComponent } from './checklist-field.component';

describe('ChecklistFieldComponent', () => {
  let component: ChecklistFieldComponent;
  let fixture: ComponentFixture<ChecklistFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChecklistFieldComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
