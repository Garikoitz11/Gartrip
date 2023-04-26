package com.example.proyecto2.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.InterfaceIdioma;
import com.example.proyecto2.R;

public class Idioma extends DialogFragment {

    InterfaceIdioma interfaceIdioma;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            interfaceIdioma = (InterfaceIdioma) context;
        }
        catch (ClassCastException e) {
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.seleccioneIdioma));

        //El array con el contenido a elegir en el dialog
        CharSequence[] idiomas = {
                getResources().getString(R.string.español),
                getResources().getString(R.string.euskera),
                getResources().getString(R.string.Ingles)
        };

        //Le añadimos el array y un clicklistener con un intent para abrir la actividad con el idioma seleccionado
        builder.setItems(idiomas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String idioma;
                if (i==0){
                    idioma = "es";
                }
                else if (i==1){
                    idioma = "eu";
                }
                else {
                    idioma = "en";
                }
                //Llamamos a la interfaz para comunicarnos con la actividad que nos ha llamado
                interfaceIdioma.cambiarIdioma(idioma);
                dismiss();
            }
        });
        return builder.create();
    }
}
