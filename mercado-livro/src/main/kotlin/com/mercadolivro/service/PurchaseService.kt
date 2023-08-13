package com.mercadolivro.service

import com.mercadolivro.Enum.BookStatus
import com.mercadolivro.events.PurchaseEvent
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.PurchaseModel
import com.mercadolivro.repository.PurchaseRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val applicationEventPublishedApi: ApplicationEventPublisher
) {

    fun create(purchaseModel: PurchaseModel){
        val invalidBooks = mutableListOf<BookModel>()

        if(!purchaseModel.books.isEmpty()) {
            purchaseModel.books.forEach { book ->
                if (book.status != BookStatus.ATIVO) {
                    invalidBooks.add(book)
                }
            }

            if (invalidBooks.isNotEmpty()) {
                val invalidBooksNames = invalidBooks.joinToString(", ") { it.name }
                throw NotFoundException(
                    "Os seguintes livros não podem ser vendidos: $invalidBooksNames",
                    "INVALID_BOOKS"
                )
            } else {
                purchaseRepository.save(purchaseModel)
                applicationEventPublishedApi.publishEvent(PurchaseEvent(this, purchaseModel))
            }
        }else{
            throw NotFoundException(
                "Livros inexistentes",
                "INVALID_BOOKS"
            )
        }
    }

    fun update(purchaseModel: PurchaseModel) {
        purchaseRepository.save(purchaseModel)

    }
}
