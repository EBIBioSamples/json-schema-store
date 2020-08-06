import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {JsonEditorComponent, JsonEditorOptions} from 'ang-jsoneditor';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit , OnDestroy {
    public editorOptions: JsonEditorOptions;
    public data: any;
    @ViewChild(JsonEditorComponent, {static: false}) editor: JsonEditorComponent;

    constructor(private storeroomClient: StoreRoomService, private route: ActivatedRoute) {
        this.editorOptions = new JsonEditorOptions();
        this.editorOptions.modes = ['code', 'text', 'tree', 'view'];
        this.editorOptions.onChange = () => console.log(this.editor.get());
        this.data = {
            $schema: 'http://json-schema.org/draft-07/schema#',
            $id: 'https://schemablocks.org/schemas/xxxxx',
            title: 'title',
            description: 'description',
            type: 'object',
            meta: {
                contributors: [
                    {
                        description: 'GA4GH Data Working Group'
                    },
                    {
                        description: 'Jules Jacobsen',
                        id: 'orcid:0000-0002-3265-15918'
                    }
                ],
                provenance: [
                    {
                        description: 'description',
                        id: 'https://github.com/phenopackets/phenopacket-schema/xxxxxx'
                    }
                ],
                used_by: [
                    {
                        description: 'Phenopackets',
                        id: 'https://github.com/phenopackets/phenopacket-schema/xxxx'
                    }
                ],
                sb_status: 'implemented'
            },
            properties: {
                term: {
                    allof: [
                        {
                            $ref: 'https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json'
                        },
                        {
                            description: 'The identifier of this disease\ne.g. MONDO:0007043, OMIM:101600, Orphanet:710, DOID:14705 (note these are all equivalent)\n',
                            examples: [
                                {
                                    id: 'MONDO:0007043'
                                }
                            ]
                        }
                    ]
                }
            },
            required: [
                'term'
            ],
            additionalProperties: false,
            examples: [
                {
                    term: {
                        id: 'MONDO:0007043',
                        label: 'Pfeiffer syndrome'
                    }
                }
            ]
        };
    }

    ngOnInit(): void {
        if (this.route.snapshot.queryParamMap && this.route.snapshot.queryParamMap.has('$id')) {
            this.getSchemaBlcok();
        }
    }

    ngOnDestroy(): void {
        this.data = null;
    }

    getSchemaBlcok(): void {
        this.route
            .queryParams
            .subscribe(params => {
                if (params) {}
                console.log(params.$id);
                this.storeroomClient.getSchemaBlockById(params.$id)
                    .subscribe((response) => {
                        this.data = response;
                    });
            });
    }

    createJsonSchema(): void {
        console.log(this.editor.get());
        this.storeroomClient.createJsonSchema(this.editor.get());
    }
}
