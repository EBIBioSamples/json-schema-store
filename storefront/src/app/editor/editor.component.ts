import {Component, OnInit, ViewChild} from '@angular/core';
import {JsonEditorComponent, JsonEditorOptions} from 'ang-jsoneditor';
import {StoreRoomService} from '../service/storeroom/store-room.service';

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {
    public editorOptions: JsonEditorOptions;
    public data: any;
    @ViewChild(JsonEditorComponent, {static: false}) editor: JsonEditorComponent;

    constructor(private storeroomClient: StoreRoomService) {
        this.editorOptions = new JsonEditorOptions();
        this.editorOptions.modes = ['code', 'text', 'tree', 'view'];
        this.editorOptions.onChange = () => console.log(this.editor.get());
        this.data = {
            products: [{
                name: 'car',
                product: [{
                    name: 'honda',
                    model: [
                        {id: 'civic', name: 'civic'},
                        {id: 'accord', name: 'accord'},
                        {id: 'crv', name: 'crv'},
                        {id: 'pilot', name: 'pilot'},
                        {id: 'odyssey', name: 'odyssey'}
                    ]
                }]
            }]
        };
    }

    ngOnInit(): void {
    }

    createJsonSchema(): void {
        console.log(this.editor.get());
        this.storeroomClient.createJsonSchema(this.editor.get());
    }
}
