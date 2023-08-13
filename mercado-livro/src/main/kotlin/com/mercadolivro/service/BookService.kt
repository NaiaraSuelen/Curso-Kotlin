package com.mercadolivro.service

import com.mercadolivro.Enum.BookStatus
import com.mercadolivro.Enum.Erros
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.BookRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    @Autowired
    private val bookRepository: BookRepository
) {

    fun getAll(pageable: Pageable): Page<BookModel> {
        return bookRepository.findAll(pageable)
    }

    fun getById(id: Int): BookModel {
        return bookRepository.findById(id).orElseThrow{(NotFoundException(Erros.ML101.message.format(id), Erros.ML101.code))}
    }

    fun getActives(pageable: Pageable): Page<BookModel> {
        return bookRepository.findByStatus(BookStatus.ATIVO, pageable)
    }

    fun create(book : BookModel) {
        bookRepository.save(book)
    }

    fun updateBook(book: BookModel) {
        bookRepository.save(book)
    }

    fun deleteBook(id: Int) {
         val book = getById(id)

        book.status = BookStatus.DELETADO

        bookRepository.save(book)
    }

    fun deleteByCustomer(customer : CustomerModel) {
        val book = bookRepository.findByCustomer(customer)

        for(book in book){
            book.status = BookStatus.DELETADO
        }
        bookRepository.saveAll(book)
    }

    fun findAllByIds(bookIds: Set<Int>): List<BookModel> {
        return bookRepository.findAllById(bookIds).toList()
    }

    fun purchase(books: MutableList<BookModel>) {
        books.map {
            it.status = BookStatus.VENDIDO
        }
        bookRepository.saveAll(books)
    }

}
