package com.house.houseserver.job.lawd;

import com.house.houseserver.core.domain.lawd.Lawd;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class LawdFieldSetMapper implements FieldSetMapper<Lawd> {

    public static final String LAWD_CODE = "lawdCode";
    public static final String LAWD_DONG = "lawdDong";
    public static final String EXIST = "exist";
    public static final String EXIST_TRUE = "존재";

    @Override
    public Lawd mapFieldSet(FieldSet fieldSet) throws BindException {
        Lawd lawd = new Lawd();
        lawd.setLawdCode(fieldSet.readString(LAWD_CODE));
        lawd.setLawdDong(fieldSet.readString(LAWD_DONG));
        lawd.setExist(fieldSet.readBoolean(EXIST, EXIST_TRUE));
        return lawd;
    }
}
