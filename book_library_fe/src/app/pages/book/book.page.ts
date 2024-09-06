import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BookListComponent } from "../../components/book/book-list/book-list.component";

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [RouterOutlet, BookListComponent],
  templateUrl: './book.page.html',
  styleUrl: './book.page.css'
})
export class BookPage {

}
