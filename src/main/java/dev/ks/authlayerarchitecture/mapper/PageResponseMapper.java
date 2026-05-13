package dev.ks.authlayerarchitecture.mapper;

import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import org.springframework.data.domain.Page;

public final class PageResponseMapper {

    private PageResponseMapper() {
        throw new UnsupportedOperationException("PageResponseMapper is Utility class");
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
