package com.psilva.android.databaseperformance.databases.implementations.greendao.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class DataGreenDao {

    @Id
    private long id;
    private String stringValue;
    private int intValue;
    private long longValue;

    @Keep
    public DataGreenDao(long id, String stringValue, int intValue, long longValue) {
        this.id = id;
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.longValue = longValue;
    }

    @Generated(hash = 884168624)
    public DataGreenDao() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }
}
