package com.example.blog.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Page<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    public static <T> Page<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        return new Page<>(content, pageNumber, pageSize, content.size(), totalPages);
    }

    public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
        List<R> transformedContent = content.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new Page<>(
                transformedContent,
                this.pageNumber,
                this.pageSize,
                this.totalElements,
                this.totalPages
        );
    }


}
