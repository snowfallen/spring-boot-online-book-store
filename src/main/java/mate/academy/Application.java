package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    private final BookService bookService;

    @Autowired
    public Application(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book1 = new Book();
            book1.setTitle("Book One");
            book1.setAuthor("Author One");
            book1.setIsbn("1111111111");
            book1.setPrice(new BigDecimal("19.99"));
            book1.setDescription("Description for Book One");
            book1.setCoverImage("http://example.com/cover1.jpg");

            Book book2 = new Book();
            book2.setTitle("Book Two");
            book2.setAuthor("Author Two");
            book2.setIsbn("2222222222");
            book2.setPrice(new BigDecimal("29.99"));
            book2.setDescription("Description for Book Two");
            book2.setCoverImage("http://example.com/cover2.jpg");

            Book book3 = new Book();
            book3.setTitle("Book Three");
            book3.setAuthor("Author Three");
            book3.setIsbn("3333333333");
            book3.setPrice(new BigDecimal("39.99"));
            book3.setDescription("Description for Book Three");
            book3.setCoverImage("http://example.com/cover3.jpg");

            Book book4 = new Book();
            book4.setTitle("Book Four");
            book4.setAuthor("Author Four");
            book4.setIsbn("4444444444");
            book4.setPrice(new BigDecimal("49.99"));
            book4.setDescription("Description for Book Four");
            book4.setCoverImage("http://example.com/cover4.jpg");

            bookService.save(book1);
            bookService.save(book2);
            bookService.save(book3);
            bookService.save(book4);

            System.out.println(bookService.findAll());
        };
    }
}
