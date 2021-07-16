import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {ChecklistEditorComponent} from "../checklist-editor/checklist-editor.component";

@Component({
  selector: 'app-checklist-field',
  templateUrl: './checklist-field.component.html',
  styleUrls: ['./checklist-field.component.scss']
})
export class ChecklistFieldComponent implements OnInit {
  @Input() parentForm: FormGroup;
  public formGroupIndex = "1";
  public checklistFieldGroup = this.fb.group({
    fieldName:[''],
    fieldType:[''],
    stringPattern:[''],
    stringFormat:[''],
    numberMax:[''],
    numberMin:['']
  });

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {

  }

  addField() {

  }

  onChecklistTypeChange(type) {

  }

}
