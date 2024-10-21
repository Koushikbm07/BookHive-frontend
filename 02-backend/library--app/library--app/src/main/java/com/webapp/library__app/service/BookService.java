package com.webapp.library__app.service;

import com.webapp.library__app.Repository.BookRepository;
import com.webapp.library__app.Repository.CheckoutRepository;
import com.webapp.library__app.Repository.HistoryRepository;
import com.webapp.library__app.entity.Book;
import com.webapp.library__app.entity.Checkout;
import com.webapp.library__app.entity.History;
import com.webapp.library__app.responsemodels.ShelfCurrentLoansResponse;
import org.hibernate.annotations.Check;

import javax.swing.text.html.Option;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BookService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private HistoryRepository historyRepository;

    public BookService(BookRepository bookRepository , CheckoutRepository checkoutRepository){
        this.bookRepository=bookRepository;
        this.checkoutRepository=checkoutRepository;
    }

    public Book checkoutBook(String userEmail , Long bookId) throws Exception{

        Optional<Book> book=bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        if(!book.isPresent() || validateCheckout!=null || book.get().getCopiesAvailable()<=0){
            throw new Exception("Book Doesn't Exist or Already Booked by User");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable()-1);
        bookRepository.save(book.get());

        Checkout checkout= new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );

        checkoutRepository.save(checkout);
        return book.get();


    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);
        if(validateCheckout!=null){
            return true;
        }
        else{
            return false;
        }

    }

    public int currentLoansCount(String userEmail){
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {

        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses=new ArrayList<>();

        List<Checkout> checkoutList=checkoutRepository.findBooksByUserEmail(userEmail);

        List<Long>  bookIdList=new ArrayList<>();

        for(Checkout checkout : checkoutList){
            bookIdList.add(checkout.getBookId());
        }

        List<Book> books=bookRepository.findBooksByBookId(bookIdList);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        for(Book book : books){
            Optional<Checkout> checkout=checkoutList.stream()
                    .filter(x -> x.getBookId()==book.getId()).findFirst();


            if(checkout.isPresent()){
                Date d1=sdf.parse(checkout.get().getReturnDate());
                Date d2=sdf.parse(LocalDate.now().toString());

                TimeUnit time=TimeUnit.DAYS;

                long diference_In_Time = time.convert(d1.getTime()-d2.getTime(),
                        TimeUnit.MILLISECONDS);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book,(int) diference_In_Time));
            }

        }
        return shelfCurrentLoansResponses;




    }


    public void returnBook(String userEmail,Long bookId){

        Optional<Book> book=bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        if(!book.isPresent() || validateCheckout==null){
            throw new Error("book doesnt exist or not checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable()+1);
        bookRepository.save(book.get());
        checkoutRepository.deleteById(validateCheckout.getId());

        History history=new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );

        historyRepository.save(history);

    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {
        Checkout validateCheckout =checkoutRepository.findByUserEmailAndBookId(userEmail,bookId);

        if(validateCheckout==null){
            throw new Error("book doesnt exist or not checked out by user");

        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date d1=sdf.parse(validateCheckout.getReturnDate());
        Date d2=sdf.parse(LocalDate.now().toString());

        if(d1.compareTo(d2) >0 || d1.compareTo(d2)==0){
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }

    }
}
