import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistEditorComponent } from './checklist-editor.component';

describe('ChecklistEditorComponent', () => {
  let component: ChecklistEditorComponent;
  let fixture: ComponentFixture<ChecklistEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChecklistEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
