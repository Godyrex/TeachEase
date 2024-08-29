import { Component } from '@angular/core';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent {
  teacherPosts = [
    { title: 'Chapter 1: Introduction to HTML', description: 'Here is an introduction to HTML basics.' },
    { title: 'Video: Bootstrap Tutorial', description: 'Watch this video to learn Bootstrap basics.' },
  ];

  sessions = [
    { title: 'HTML Basics', date: new Date(2024, 7, 26) },
    { title: 'CSS Styling', date: new Date(2024, 7, 28) },
  ];

  presenceHistory = [
    { date: new Date(2024, 7, 22), status: 'Present' },
    { date: new Date(2024, 7, 21), status: 'Absent' },
  ];

  additionalInfo = ['Class Notes', 'Assignments', 'Extra Resources'];

  newPost = { title: '', description: '' };
  newSession = { title: '', date: new Date };

  displayedColumns: string[] = ['date', 'status'];

  addPost() {
    if (this.newPost.title && this.newPost.description) {
      this.teacherPosts.push({ ...this.newPost });
      this.newPost = { title: '', description: '' };
    }
  }

  editPost(post: any) {
    // Implement your edit logic here
  }

  deletePost(post: any) {
    this.teacherPosts = this.teacherPosts.filter((p) => p !== post);
  }

  addSession() {
    if (this.newSession.title && this.newSession.date) {
      this.sessions.push(this.newSession);
      this.newSession = { title: '', date: new Date };
    }
  }

  editSession(session: any) {
    // Implement your edit logic here
  }

  deleteSession(session: any) {
    this.sessions = this.sessions.filter((s) => s !== session);
  }
}
