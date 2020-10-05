import {Injectable} from '@angular/core';
import * as fileSaver from 'file-saver';
import {toXML} from 'jstoxml';

@Injectable({
    providedIn: 'root'
})
export class JsonConverterService {

    constructor() {
    }

    public jsonToJSONAndDownload(jsonObject: any, fileName?: string): void {
        if (!fileName) {
            fileName = jsonObject.title + '-' + jsonObject.$id.substring(jsonObject.$id.lastIndexOf('/')) + '.json';
        }
        JSON.stringify(jsonObject);
        const file = new Blob([JSON.stringify(jsonObject, null, 2)], {type: 'text/javascript'});
        fileSaver.saveAs(file, fileName);
    }

    public jsonToXMLAndDownload(jsonObject: any, fileName?: string): void {
        if (!fileName) {
            fileName = jsonObject.title + '-' + jsonObject.$id.substring(jsonObject.$id.lastIndexOf('/')) + '.xml';
        }
        const xmlOptions = {
            header: false,
            indent: '  '
        };
        const file = new Blob([toXML(jsonObject, xmlOptions)], {type: 'text/xml'});
        fileSaver.saveAs(file, fileName);
    }
}
