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
    private static checklistFieldGroup = {
        fieldName: [''],
        fieldDescription: [''],
        fieldType: ['string'],
        stringFormat: [''],
        stringPattern: [''],
        stringMinLength: [''],
        stringMaxLength: [''],
        numberMin: [''],
        numberMax: [''],
        numberMultiples: [''],
        enumList: [''],
        const: [''],
        fieldRequired: [''],
        fieldRecommended: ['']
    };

    public fieldType: string;

    constructor(private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.fieldType = 'string';
    }

    public static getChecklistFieldGroupTemplate() {
        return ChecklistFieldComponent.checklistFieldGroup;
    }

    onChecklistTypeChange(event) {
        this.fieldType = event.value;
    }

}
