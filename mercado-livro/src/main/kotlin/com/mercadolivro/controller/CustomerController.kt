package com.mercadolivro.controller

import com.mercadolivro.controller.request.PostCustomerRequest
import com.mercadolivro.controller.request.PutCustomerRequest
import com.mercadolivro.controller.response.CustomerResponse
import com.mercadolivro.extension.toCustomerModel
import com.mercadolivro.extension.toResponse
import com.mercadolivro.service.CustomerService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("customer")
class CustomerController(
    val customerService: CustomerService
){
    @GetMapping
    fun getAll(@PageableDefault(page = 0, size = 10 ) pageable: Pageable, @RequestParam name: String?): Iterable<CustomerResponse> {
        return customerService.getAll(name, pageable).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Int): CustomerResponse{
        return customerService.getById(id).toResponse()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: PostCustomerRequest){
        customerService.saveCustomer(request.toCustomerModel())
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable id: Int, @RequestBody @Valid customer: PutCustomerRequest){
        val customerSaved = customerService.getById(id)
        customerService.updateCustomer(id, customer.toCustomerModel(customerSaved))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        customerService.deleteCustomer(id)
    }
}