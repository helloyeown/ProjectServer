package com.project.apiserver.productboard.repository.search;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.project.apiserver.common.PageRequestDTO;
import com.project.apiserver.common.PageResponseDTO;
import com.project.apiserver.common.QProductCategory;
import com.project.apiserver.member.entity.QMemberAccount;
import com.project.apiserver.productboard.dto.ProductListDTO;
import com.project.apiserver.productboard.entity.Product;
import com.project.apiserver.productboard.entity.QProduct;
import com.project.apiserver.productboard.entity.QProductImage;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {
    
    public ProductSearchImpl() {
        super(Product.class);
    }

    

    @Override
    public PageResponseDTO<ProductListDTO> search(PageRequestDTO requestDTO) {

        // pageable 생성
        Pageable pageable = makePageable(requestDTO);

        QProduct product = QProduct.product;
        QMemberAccount member = QMemberAccount.memberAccount;
        QProductCategory category = QProductCategory.productCategory;
        QProductImage image = QProductImage.productImage;

        String keyword = requestDTO.getKeyword();
        String type = requestDTO.getType();
        Integer procateno = 1;

        procateno = requestDTO.getCateno();

        // JPQLQuery 생성
        JPQLQuery<Product> searchQuery = from(product);

        // join
        searchQuery.leftJoin(product.category,category);
        searchQuery.leftJoin(product.member, member);
        searchQuery.leftJoin(product.images, image);


        // where 조건
        searchQuery.where(product.delFlag.eq(Boolean.FALSE));

        // 검색 조건이 있을 때
        if(keyword != null && type != null) {

            BooleanBuilder searchBuilder = new BooleanBuilder();
            String[] searchArr = type.split("");

            for (String typeword : searchArr) {
                
                switch (typeword) {
                    case "n" -> searchBuilder.or(product.pname.contains(keyword));      // 상품이름
                    case "d" -> searchBuilder.or(product.pdesc.contains(keyword));      // 상품설명
                    case "m" -> searchBuilder.or(product.member.nickname.contains(keyword));    // 닉네임
                }
            }
            searchQuery.where(searchBuilder);
        }

        this.getQuerydsl().applyPagination(pageable, searchQuery);

        // select
        JPQLQuery<ProductListDTO> listQuery = searchQuery.select(Projections.bean(
            ProductListDTO.class,
            product.pno,
            product.delFlag,
            product.pdesc,
            product.pname,
            product.price,
            product.modDate,
            product.member.mno,
            product.member.email,
            product.member.nickname,
            product.member.roleName,
            product.category.procatename,
            product.category.procateno
        ));

        long totalCount = listQuery.fetchCount();
        List<ProductListDTO> list = listQuery.fetch();
        log.info(list);

        return new PageResponseDTO<>(list, totalCount, requestDTO);

    }

}
