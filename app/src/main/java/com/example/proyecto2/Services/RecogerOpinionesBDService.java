package com.example.proyecto2.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class RecogerOpinionesBDService extends Worker {
    private Context context;
    public RecogerOpinionesBDService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        //Declaramos la variable para devolver el resultado de la consulta
        Data resultados = new Data.Builder()
                .build();;

        //Obtenemos los datos enviados desde la actividad
        String hotel = getInputData().getString("hotel_id");

        //Realizamos la conexion con el servidor (con el php a ejecutar)
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/gartrip/obtenerOpiniones.php";
        HttpURLConnection urlConnection = null;
        URL destino = null;
        try {
            destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);

            //Le añadimos los parametros a la llamada al servidor
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("hotel", hotel);
            String parametros = builder.build().getEncodedQuery();

            //Especificamos el tipo de peticion
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Envia los parametros a la url indicada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Espera a la respues del servidor
            int statusCode = urlConnection.getResponseCode();
            Log.i("RESPUESTA", String.valueOf(statusCode));
            //Si la respuesta es correcta lee el resultado y lo almacena en la variable de tipo data para devolverlo a la actividad
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                Log.d("Opiniones", result);

                //Obtenemos la info del json devuelto por el servidor
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                boolean operacionCorrecta = (boolean) json.get("operacionCorrecta");
                JSONArray jsonArray = (JSONArray) json.get("opiniones");
                Log.d("Opiniones", String.valueOf(jsonArray));
                Log.d("Opiniones", String.valueOf(jsonArray.size()));

                ArrayList<String> nombres = new ArrayList<>();
                ArrayList<String> comentarios = new ArrayList<>();
                ArrayList<Float> puntuacion = new ArrayList<>();

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    nombres.add((String) object.get("nombre"));
                    comentarios.add((String) object.get("comentario"));
                    puntuacion.add(Float.valueOf((String) object.get("puntuacion")));
                }

                Log.d("Opiniones", String.valueOf(nombres));
                Log.d("Opiniones", String.valueOf(nombres.size()));
                Log.d("Opiniones", String.valueOf(comentarios));
                Log.d("Opiniones", String.valueOf(comentarios.size()));
                Log.d("Opiniones", String.valueOf(puntuacion));
                Log.d("Opiniones", String.valueOf(puntuacion.size()));

                String[] miarraydenombres = new String[nombres.size()];
                miarraydenombres = nombres.toArray(miarraydenombres);
                String[] miarraydecomentarios = new String[comentarios.size()];
                miarraydecomentarios = comentarios.toArray(miarraydecomentarios);
                float[] miarraydepuntuacion = convertFloat(puntuacion);
                Log.d("Opiniones", Arrays.toString(miarraydepuntuacion));

                resultados = new Data.Builder()
                        .putBoolean("operacionCorrecta", operacionCorrecta)
                        .putStringArray("nombres",miarraydenombres)
                        .putStringArray("comentarios",miarraydecomentarios)
                        .putFloatArray("puntuaciones",miarraydepuntuacion)
                        .build();
                inputStream.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Se devuelve el objeto de tipo data
        return Result.success(resultados);
    }

    public static float[] convertFloat(ArrayList<Float> integers)
    {
        float[] ret = new float[integers.size()];
        Iterator<Float> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().floatValue();
        }
        return ret;
    }
}
