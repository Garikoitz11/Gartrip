package com.example.proyecto2.Services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class OpinionBDService extends Worker {

    public OpinionBDService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String email = getInputData().getString("email");
        String hotel_id = getInputData().getString("id");
        String dir = getInputData().getString("accion");

        String parametro = "";
        String direccion = "";
        //Recoge la direccion del archivo dependiendo de la accion a realizar
        if (Objects.equals(dir, "comprobar")) {
            parametro = "email=" + email + "&hotel=" + hotel_id;
            direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/gartrip/comprobarOpinion.php";
            Log.i("JSON", "Se accede a comprobar.php");
        } else if (Objects.equals(dir, "crear")) {
            String coment = getInputData().getString("comentario");
            String puntos = getInputData().getString("puntos");
            parametro = "email=" + email + "&hotel=" + hotel_id + "&comentario=" + coment + "&puntos=" + puntos;
            Log.i("JSON", parametro);
            direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/gartrip/crearOpinion.php";
            Log.i("JSON", "Se accede a crear.php");
        }

        //Se realiza la consulta a la direccion
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Se recoge el json recibido
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametro);
            out.flush();

            int statusCode = urlConnection.getResponseCode();
            Log.i("JSON", String.valueOf(statusCode));

            if (statusCode == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String response = stringBuilder.toString();
                Log.i("JSON", response);

                // Procesar la respuesta según las necesidades
                JSONObject jsonResponse = new JSONObject(response);

                //value devuelve true si no hubo error y false si ocurrio alguno.
                Boolean resultado = (Boolean) jsonResponse.get("value");
                Log.i("JSON", "¿Correcto? " + resultado);

                String mensaje = (String) jsonResponse.get("mens");
                Log.i("JSON", mensaje);

                reader.close();
                inputStream.close();
                out.close();
                urlConnection.disconnect();

                Data datos = new Data.Builder()
                            .putBoolean("valor",resultado)
                            .putString("texto",mensaje)
                            .build();

                    return Result.success(datos);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
