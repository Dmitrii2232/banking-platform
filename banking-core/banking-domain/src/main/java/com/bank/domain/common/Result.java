package com.bank.domain.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Result<T, E> {
    
    private Result() {}
    
    public static <T, E> Result<T, E> success(T value) {
        return new Success<>(Objects.requireNonNull(value, "Значение успеха не может быть null"));
    }
    
    public static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(Objects.requireNonNull(error, "Ошибка не может быть null"));
    }
    
    public static <T, E> Result<T, E> failure(E error, List<String> reasons) {
        return new Failure<>(Objects.requireNonNull(error), reasons != null ? reasons : List.of());
    }
    
    public abstract boolean isSuccess();
    public abstract boolean isFailure();
    public abstract T getValue();
    public abstract E getError();
    public abstract List<String> getReasons();
    
    public abstract <U> Result<U, E> map(Function<? super T, ? extends U> mapper);
    public abstract <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper);
    
    public T getValueOrElse(T defaultValue) {
        return isSuccess() ? getValue() : defaultValue;
    }
    
    public T getValueOrThrow() {
        if (isFailure()) {
            throw new NoSuchElementException("Result содержит ошибку: " + getError());
        }
        return getValue();
    }
    
    public void ifSuccess(Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(getValue());
        }
    }
    
    public void ifFailure(Consumer<? super E> action) {
        if (isFailure()) {
            action.accept(getError());
        }
    }
    
    private static final class Success<T, E> extends Result<T, E> {
        private final T value;
        
        Success(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isSuccess() { return true; }
        
        @Override
        public boolean isFailure() { return false; }
        
        @Override
        public T getValue() { return value; }
        
        @Override
        public E getError() {
            throw new NoSuchElementException("Успешный результат не содержит ошибки");
        }
        
        @Override
        public List<String> getReasons() { return Collections.emptyList(); }
        
        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return new Success<>(mapper.apply(value));
        }
        
        @Override
        public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
            return mapper.apply(value);
        }
    }
    
    private static final class Failure<T, E> extends Result<T, E> {
        private final E error;
        private final List<String> reasons;
        
        Failure(E error) {
            this.error = error;
            this.reasons = new ArrayList<>();
        }
        
        Failure(E error, List<String> reasons) {
            this.error = error;
            this.reasons = new ArrayList<>(reasons);
        }
        
        @Override
        public boolean isSuccess() { return false; }
        
        @Override
        public boolean isFailure() { return true; }
        
        @Override
        public T getValue() {
            throw new NoSuchElementException("Ошибочный результат не содержит значения: " + error);
        }
        
        @Override
        public E getError() { return error; }
        
        @Override
        public List<String> getReasons() { return Collections.unmodifiableList(reasons); }
        
        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return new Failure<>(error, reasons);
        }
        
        @Override
        public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
            return new Failure<>(error, reasons);
        }
    }
}