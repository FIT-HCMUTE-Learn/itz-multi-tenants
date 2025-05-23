package com.multi.tenants.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseListDto<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T content;
    private Long totalElements;
    private Integer totalPages;
}
