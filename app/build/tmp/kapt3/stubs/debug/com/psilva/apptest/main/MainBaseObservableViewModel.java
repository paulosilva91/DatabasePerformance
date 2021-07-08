package com.psilva.apptest.main;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b&\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0006\u0010\u000e\u001a\u00020\u000bJ\u000e\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0010J\u0010\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/psilva/apptest/main/MainBaseObservableViewModel;", "Landroidx/lifecycle/ViewModel;", "Landroidx/databinding/Observable;", "()V", "mCallBacks", "Landroidx/databinding/PropertyChangeRegistry;", "getMCallBacks", "()Landroidx/databinding/PropertyChangeRegistry;", "mCallBacks$delegate", "Lkotlin/Lazy;", "addOnPropertyChangedCallback", "", "callback", "Landroidx/databinding/Observable$OnPropertyChangedCallback;", "notifyChange", "viewId", "", "removeOnPropertyChangedCallback", "app_debug"})
public abstract class MainBaseObservableViewModel extends androidx.lifecycle.ViewModel implements androidx.databinding.Observable {
    private final transient kotlin.Lazy mCallBacks$delegate = null;
    
    private final androidx.databinding.PropertyChangeRegistry getMCallBacks() {
        return null;
    }
    
    @java.lang.Override()
    public void addOnPropertyChangedCallback(@org.jetbrains.annotations.NotNull()
    androidx.databinding.Observable.OnPropertyChangedCallback callback) {
    }
    
    @java.lang.Override()
    public void removeOnPropertyChangedCallback(@org.jetbrains.annotations.NotNull()
    androidx.databinding.Observable.OnPropertyChangedCallback callback) {
    }
    
    public final void notifyChange() {
    }
    
    public final void notifyChange(int viewId) {
    }
    
    public MainBaseObservableViewModel() {
        super();
    }
}