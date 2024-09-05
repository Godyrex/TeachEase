package org.example.teacheaseapplication.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedPostResponse {
    private List<PostResponse> postResponses;
    private int totalPages;
    private int currentPage;
    private long totalElements;
    private int pageSize;
}
