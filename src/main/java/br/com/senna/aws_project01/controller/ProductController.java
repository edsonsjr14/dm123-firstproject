package br.com.senna.aws_project01.controller;

import br.com.senna.aws_project01.enums.EventType;
import br.com.senna.aws_project01.model.Product;
import br.com.senna.aws_project01.repository.ProductRepository;

import br.com.senna.aws_project01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductRepository productRepository;
    private ProductPublisher productPublisher;

    @Autowired
    public ProductController(
            ProductRepository productRepository,
            ProductPublisher productPublisher
    ) {

        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if(optProduct.isPresent()) {
            return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(
            @RequestBody @Valid Product product
    ) {
        Product productSaved = productRepository.save(product);
        this.productPublisher.publishProductEvent(productSaved, EventType.PRODUCT_CREATED, "edson");
        return new ResponseEntity<Product>(productSaved, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct (
            @RequestBody @Valid Product product, @PathVariable("id") long id
    ) {
        if(productRepository.existsById(id)) {
            product.setId(id);
            Product productSaved = productRepository.save(product);
            this.productPublisher.publishProductEvent(productSaved, EventType.PRODUCT_UPDATED, "senna");
            return new ResponseEntity<Product>(productSaved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            productRepository.delete(product);
            this.productPublisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "junior");
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code);
        if (optProduct.isPresent()) {
            return new ResponseEntity<Product>(optProduct.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
