package com.jalloft.eventmaster.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.jalloft.eventmaster.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String DATE_PATTERN_DMMMYYYY = "dd 'de' MMMM 'de' yyyy";
    public static final String DATE_PATTERN_DMMYYYY = "dd 'de' MMM 'de' yyyy";
    public static final String DATE_PATTERN_HHMM = "HH:mm";
    private static final long TWO_HOUR_MILLIS = 2 * 60 * 60 * 1000;

    public static String convertDate(Date date, String pattern) {
        SimpleDateFormat formatador = new SimpleDateFormat(pattern, new Locale("pt", "BR"));
        return formatador.format(date);
    }

    public static String convertDate(Date date) {
        SimpleDateFormat formatador = new SimpleDateFormat(DATE_PATTERN_DMMMYYYY, new Locale("pt", "BR"));
        return formatador.format(date);
    }

    public static String convertDateAndTime(Date date) {
        SimpleDateFormat formatoData = new SimpleDateFormat(DATE_PATTERN_DMMMYYYY, new Locale("pt", "BR"));
        SimpleDateFormat formatoHora = new SimpleDateFormat(DATE_PATTERN_HHMM, new Locale("pt", "BR"));
        long horaInicial = date.getTime();
        long horaFinal = horaInicial + (TWO_HOUR_MILLIS);
        String dataFormatada = formatoData.format(date);
        String horaInicialFormatada = formatoHora.format(date);
        String horaFinalFormatada = formatoHora.format(new Date(horaFinal));

        return dataFormatada.concat(" das ").concat(horaInicialFormatada).concat(" às ").concat(horaFinalFormatada);
    }


    public static Drawable colorLectureBg(Context context, String eventName) {
        VectorDrawable vetorDrawable = (VectorDrawable) ActivityCompat.getDrawable(context, R.drawable.events_lecture_bg);
        if (vetorDrawable != null) {
            Drawable mutableDrawable = vetorDrawable.mutate();
            DrawableCompat.setTint(mutableDrawable, ColorUtils.getColor(eventName));
            return mutableDrawable;
        }
        return null;
    }

}
