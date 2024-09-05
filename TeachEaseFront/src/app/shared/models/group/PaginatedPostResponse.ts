import {PostResponse} from "./PostResponse";

export interface PaginatedPostResponse {
    postResponses: PostResponse[];
    currentPage: number;
    totalPages: number;
    totalElements: number;
    pageSize: number;
}