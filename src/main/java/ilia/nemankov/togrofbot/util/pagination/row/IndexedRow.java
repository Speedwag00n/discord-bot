package ilia.nemankov.togrofbot.util.pagination.row;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class IndexedRow extends Row {

    private int index;

    public IndexedRow(String content) {
        super(content);
    }

}
