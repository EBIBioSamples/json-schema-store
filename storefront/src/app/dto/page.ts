export interface Page {
    content: any[];
    pageable: object;
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    sort: { unsorted: boolean, sorted: boolean, empty: boolean };
    first: boolean;
    numberOfElements: number;
    empty: boolean;
}
