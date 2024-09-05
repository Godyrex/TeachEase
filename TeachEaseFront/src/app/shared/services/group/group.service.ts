import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GroupRequest } from '../../models/group/GroupRequest';
import { GroupResponse } from '../../models/group/GroupResponse';
import {PostRequest} from "../../models/group/PostRequest";
import {PostResponse} from "../../models/group/PostResponse";
import {PaginatedPostResponse} from "../../models/group/PaginatedPostResponse";

@Injectable({
  providedIn: 'root'
})
export class GroupService {
  private apiUrl = 'http://localhost:8080/api/v1/group';

  constructor(private http: HttpClient) {}

  createGroup(groupRequest: GroupRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, groupRequest);
  }

  getGroups(): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>(this.apiUrl);
  }

  getGroup(groupId: string): Observable<GroupResponse> {
    return this.http.get<GroupResponse>(`${this.apiUrl}/${groupId}`);
  }

  deleteGroup(groupId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${groupId}`);
  }
  leaveGroup(groupId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${groupId}/leave`, null);
  }

  updateGroup(groupId: string, groupRequest:GroupRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${groupId}`, groupRequest);
  }
  downloadFile(groupId: string,postId: string, fileName: string): Observable<Blob> {
    console.log(`${this.apiUrl}/${groupId}/${postId}/${fileName}/download`);
    return this.http.get(`${this.apiUrl}/${groupId}/${postId}/${fileName}/download`, { responseType: 'blob' });
  }
  addPost(groupId: string, coursePostRequest: PostRequest, files: File[]): Observable<void> {
    const formData: FormData = new FormData();
    // Append the coursePostRequest fields to the form data
    formData.append('title', coursePostRequest.title);
    formData.append('content', coursePostRequest.content);
    // Append each file to the form data
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i], files[i].name);
    }

    return this.http.put<void>(`${this.apiUrl}/${groupId}/addPost`, formData);
  }
  getPost(groupId: string, postId: string): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${this.apiUrl}/${groupId}/post/${postId}`);
  }
  getPosts(groupId: string,currentPage: number,pageSize: number): Observable<PaginatedPostResponse> {
    return this.http.get<PaginatedPostResponse>(`${this.apiUrl}/${groupId}/posts`, { params: { page: currentPage.toString(), size: pageSize.toString() } });
  }
  deletePost(groupId: string, postID: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${groupId}/deletePost`, { params: { postID } });
  }

}