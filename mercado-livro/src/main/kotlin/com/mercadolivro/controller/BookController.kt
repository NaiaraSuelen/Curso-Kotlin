package com.mercadolivro.controller


import com.mercadolivro.controller.request.PostBookRequest
import com.mercadolivro.controller.request.PutBookRequest
import com.mercadolivro.controller.response.BookResponse
import com.mercadolivro.extension.toBookModel
import com.mercadolivro.extension.toResponse
import com.mercadolivro.service.BookService
import com.mercadolivro.service.CustomerService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("book")
class BookController(
    val bookService: BookService,
    val customerService: CustomerService
){

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: PostBookRequest){
        val customer = customerService.getById(request.customerId)
        bookService.create(request.toBookModel(customer))
    }
    
    @GetMapping
    fun getAll(@PageableDefault(page=0, size=10)pageable: Pageable): Page<BookResponse> {
        return bookService.getAll(pageable).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getId(@PathVariable id: Int): BookResponse{
        return bookService.getById(id).toResponse()
    }

    @GetMapping("/active")
    fun getActive(@PageableDefault(page = 0, size = 10) pageable: Pageable): Page<BookResponse>{
        return bookService.getActives(pageable).map { it.toResponse() }
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable id: Int, @RequestBody book: PutBookRequest ){
        val bookSaved = bookService.getById(id)
        bookService.updateBook(book.toBookModel(bookSaved))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        bookService.deleteBook(id)
    }
}