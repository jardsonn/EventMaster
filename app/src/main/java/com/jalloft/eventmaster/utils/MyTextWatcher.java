package com.jalloft.eventmaster.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class MyTextWatcher implements TextWatcher {

    @Override
    public abstract void onTextChanged(CharSequence s, int start, int before, int count);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
