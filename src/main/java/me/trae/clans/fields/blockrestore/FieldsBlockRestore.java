package me.trae.clans.fields.blockrestore;

import me.trae.clans.fields.FieldsData;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.core.blockrestore.BlockRestore;

public class FieldsBlockRestore extends BlockRestore {

    public static final String NAME = "Fields";

    public FieldsBlockRestore(final FieldsData fieldsData, final FieldsBlock fieldsBlock, final long duration) {
        super(NAME, fieldsData.getLocation(), fieldsBlock.getReplacementBlockId(), duration);
    }
}