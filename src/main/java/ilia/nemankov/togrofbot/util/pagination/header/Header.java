package ilia.nemankov.togrofbot.util.pagination.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public abstract class Header {

    private int pageNumber;
    private int maxPageNumber;

}
