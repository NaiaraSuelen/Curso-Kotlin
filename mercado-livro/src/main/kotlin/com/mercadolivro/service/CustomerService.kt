package com.mercadolivro.service

import com.mercadolivro.Enum.CustomerStatus
import com.mercadolivro.Enum.Erros
import com.mercadolivro.Enum.Profile
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.repository.CustomerRepository
import com.mercadolivro.model.CustomerModel
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Service
class CustomerService (
    @Autowired
    private val customerRepository: CustomerRepository,
    private val bookService : BookService,
    private val bCrypt: BCryptPasswordEncoder
){
    fun getAll(name: String?, pageable: Pageable): Page<CustomerModel> {
        return if (!name.isNullOrEmpty()) {
            customerRepository.findByNameContaining(name, pageable)
        } else {
            customerRepository.findAll(pageable)
        }
    }

    fun getById(id: Int): CustomerModel {
        return customerRepository.findById(id).orElseThrow { NotFoundException(Erros.ML201.message.format(id), Erros.ML201.code) }
    }

    fun saveCustomer(customer: CustomerModel){
        val customerCopy = customer.copy(
            roles = setOf(Profile.CUSTOMER),
            password = bCrypt.encode(customer.password)
        )
        customerRepository.save(customerCopy)
    }

    fun deleteCustomer(id: Int){
        val customer = getById(id)
        bookService.deleteByCustomer(customer)

        customer.status = CustomerStatus.INATIVO

        customerRepository.deleteById(id)
        customerRepository.save(customer)
    }

    fun updateCustomer(id: Int, customer: CustomerModel){
        val newCustomer = customerRepository.findById(id).orElseThrow { EntityNotFoundException("Customer não encontrado para o ID: $id") }

        newCustomer.name = customer.name

        newCustomer.email = customer.email

        customerRepository.save(newCustomer)
    }

    fun emailAvailable(email: String): Boolean {
       return !customerRepository.existsByEmail(email)
    }
}