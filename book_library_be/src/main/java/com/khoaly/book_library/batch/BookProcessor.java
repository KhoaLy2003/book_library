package com.khoaly.book_library.batch;

import com.khoaly.book_library.entity.Book;
import com.khoaly.book_library.entity.BookInventory;
import com.khoaly.book_library.enumeration.BookStatusEnum;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.item.ItemProcessor;

public class BookProcessor implements ItemProcessor<Book,Book> {
    @Override
    public Book process(@NonNull Book book) throws Exception {
        book.setStatus(BookStatusEnum.AVAILABLE);

        BookInventory bookInventory = BookInventory
                .builder()
                .totalQuantity(NumberUtils.INTEGER_ONE)
                .currentQuantity(NumberUtils.INTEGER_ONE)
                .book(book)
                .build();

        book.setBookInventory(bookInventory);

        return book;
    }
}
