package com.project.apiserver.productboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.apiserver.productboard.dto.ProductDTO;
import com.project.apiserver.productboard.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;

    // 목록
    @GetMapping("/list")
    public List<ProductDTO> getList(){

        return productService.getList();

    }

    // 조회
    @GetMapping("/{pno}")
    public ProductDTO readOne(@PathVariable Long pno){

        return productService.readOne(pno);

    }

    // 등록
    @PostMapping("/")
    public Map<String, String> register(@RequestBody ProductDTO dto){

        log.info("-------------------------dto");
        log.info(dto);
        productService.register(dto);

        return Map.of("result", "success");

    }

    // 삭제
    @DeleteMapping("/{pno}")
    public Map<String, String> delete(@PathVariable Long pno){

        productService.delete(pno);

        return Map.of("result", "success");

    }

    // 수정
    @PutMapping("/")
    public Map<String, String> modify(@RequestBody ProductDTO dto){

        productService.modify(dto);

        return Map.of("result", "success");

    }

}
