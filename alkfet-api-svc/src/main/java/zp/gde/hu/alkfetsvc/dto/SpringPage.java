package zp.gde.hu.alkfetsvc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Spring Data Page JSON válaszának deszerializálásához használt wrapper.
 * A db-svc Page<T> típusú választ ad vissza, ezt mappelje ebbe.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpringPage<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public PageResponse<T> toPageResponse() {
        return PageResponse.<T>builder()
                .content(content)
                .page(number)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .build();
    }
}

