package com.jalloft.eventmaster.ui.states;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class ResponseUiState<T> {

    public static final class Loading<T> extends ResponseUiState<T> {
    }

    public static final class Success<T> extends ResponseUiState<T> {
        @NonNull
        private final T data;

        public Success(@NonNull T data) {
            this.data = data;
        }

        @NonNull
        public T getData() {
            return data;
        }
    }

    public static final class Failure<T> extends ResponseUiState<T> {
        private T data;
        private @StringRes int erroMessage;

        public Failure(T data) {
            this.data = data;
        }

        public Failure(@StringRes int erroMessage) {
            this.erroMessage = erroMessage;
        }


        public T getData() {
            return data;
        }

        @StringRes
        public int getErroMessage() {
            return erroMessage;
        }
    }

}
