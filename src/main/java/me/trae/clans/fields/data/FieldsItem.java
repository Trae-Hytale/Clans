package me.trae.clans.fields.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldsItem {

    private final String id;
    private final int quantity, chance;

    public FieldsItem(final String id, final int amount) {
        this(id, amount, 100);
    }
}