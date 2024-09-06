export interface Book {
  id: number | undefined;
  title: string | undefined;
  isbn13: string | undefined;
  numPages: number | undefined;
  publicationDate: string | undefined;
  status: BookStatusEnum | undefined;
  currentQuantity: number | undefined;
}

export interface BookDetail {
  id: number | undefined;
  title: string | undefined;
  authors: string | undefined;
  averageRating: number | undefined;
  isbn: string | undefined;
  isbn13: string | undefined;
  languageCode: string | undefined;
  numPages: number | undefined;
  publicationDate: string | undefined;
  publisher: string | undefined;
  status: BookStatusEnum | undefined;
  totalQuantity: number | undefined;
  currentQuantity: number | undefined;
}

export function mapBookDetailToBook(bookDetail: BookDetail): Book {
  return {
    id: bookDetail.id,
    title: bookDetail.title,
    isbn13: bookDetail.isbn13,
    numPages: bookDetail.numPages,
    publicationDate: bookDetail.publicationDate,
    status: bookDetail.status,
    currentQuantity: bookDetail.currentQuantity,
  };
}

export enum BookStatusEnum {
  AVAILABLE = 'AVAILABLE',
  BORROWED = 'BORROWED',
  RESERVED = 'RESERVED',
  LOST = 'LOST',
  NORMAL = 'NORMAL',
  DAMAGED = 'DAMAGED',
  CAN_NOT_BORROW = 'CAN_NOT_BORROW',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
}

export const sampleBooks: Book[] = [
  {
    id: 1,
    title: 'The Great Gatsby',
    isbn13: '9780743273565',
    numPages: 180,
    publicationDate: '1925-04-10',
    status: BookStatusEnum.AVAILABLE,
    currentQuantity: 0,
  },
  {
    id: 2,
    title: 'To Kill a Mockingbird',
    isbn13: '9780061120084',
    numPages: 281,
    publicationDate: '1960-07-11',
    status: BookStatusEnum.AVAILABLE,
    currentQuantity: 0,
  },
  {
    id: 3,
    title: '1984',
    isbn13: '9780451524935',
    numPages: 328,
    publicationDate: '1949-06-08',
    status: BookStatusEnum.AVAILABLE,
    currentQuantity: 0,
  },
];
