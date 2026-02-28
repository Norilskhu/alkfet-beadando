package zp.gde.hu.alkfetsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Általános lapozható válasz wrapper.
 *
 * @param <T> az egyes elemek típusa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** Az aktuális oldal elemei */
    private List<T> content;

    /** Aktuális oldalszám (0-alapú) */
    private int page;

    /** Oldal mérete */
    private int size;

    /** Összes elem száma */
    private long totalElements;

    /** Összes oldal száma */
    private int totalPages;

    /** Ez az első oldal? */
    private boolean first;

    /** Ez az utolsó oldal? */
    private boolean last;
}

