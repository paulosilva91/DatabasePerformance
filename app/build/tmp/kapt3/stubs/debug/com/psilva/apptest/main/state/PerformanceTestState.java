package com.psilva.apptest.main.state;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J(\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0006J\u0006\u0010\u0012\u001a\u00020\u0004J\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u0014J\u0006\u0010\u0015\u001a\u00020\u0006J\u000e\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u0004J\u000e\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/psilva/apptest/main/state/PerformanceTestState;", "", "()V", "_databaseOperationTypeEnum", "Lcom/psilva/android/databaseperformance/databases/enums/DatabaseOperationTypeEnum;", "_quantityTestToRun", "", "_testList", "", "Lcom/psilva/android/databaseperformance/databases/export/csv/model/DatabaseResultModel;", "addDatabaseTest", "", "databaseOperationEnum", "Lcom/psilva/android/databaseperformance/databases/enums/DatabaseOperationEnum;", "resultModel", "Lcom/psilva/apptest/main/ResultDataModel;", "quantityData", "time", "getDatabaseOperationType", "getDatabasePerformanceTest", "", "getQuantityTestToRun", "setDatabaseOperationType", "databaseOperationType", "setQuantityTestToRun", "quantityTestToRun", "app_debug"})
public final class PerformanceTestState {
    private long _quantityTestToRun = 0L;
    private com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum _databaseOperationTypeEnum;
    private java.util.List<com.psilva.android.databaseperformance.databases.export.csv.model.DatabaseResultModel> _testList;
    
    public final void addDatabaseTest(@org.jetbrains.annotations.NotNull()
    com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum databaseOperationEnum, @org.jetbrains.annotations.Nullable()
    com.psilva.apptest.main.ResultDataModel resultModel, long quantityData, long time) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Collection<com.psilva.android.databaseperformance.databases.export.csv.model.DatabaseResultModel> getDatabasePerformanceTest() {
        return null;
    }
    
    public final long getQuantityTestToRun() {
        return 0L;
    }
    
    public final void setQuantityTestToRun(long quantityTestToRun) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum getDatabaseOperationType() {
        return null;
    }
    
    public final void setDatabaseOperationType(@org.jetbrains.annotations.NotNull()
    com.psilva.android.databaseperformance.databases.enums.DatabaseOperationTypeEnum databaseOperationType) {
    }
    
    public PerformanceTestState() {
        super();
    }
}