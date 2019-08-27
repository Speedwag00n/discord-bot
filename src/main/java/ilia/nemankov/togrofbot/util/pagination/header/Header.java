package ilia.nemankov.togrofbot.util.pagination.header;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class Header {

    private int pageNumber;
    private int maxPageNumber;

}
