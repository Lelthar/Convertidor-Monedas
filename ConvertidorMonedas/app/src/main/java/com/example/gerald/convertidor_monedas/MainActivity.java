package com.example.gerald.convertidor_monedas;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;



import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {
    private SoapPrimitive resultadoXML;
    private TextView salida_textview;
    double valor_dolar;

    private void obtenerValorDolarWS() {
        String soapAction = "http://ws.sdde.bccr.fi.cr/ObtenerIndicadoresEconomicosXML";
        String namespace = "http://ws.sdde.bccr.fi.cr";
        String url = "http://indicadoreseconomicos.bccr.fi.cr/indicadoreseconomicos/WebServices/wsIndicadoresEconomicos.asmx";
        String metodo_nombre = "ObtenerIndicadoresEconomicosXML";
        String fecha;
        DateFormat formato_fecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha_actual = new Date();
        fecha = formato_fecha.format(fecha_actual);
        try {
            SoapObject request = new SoapObject(namespace, metodo_nombre);
            request.addProperty("tcIndicador", "317");
            request.addProperty("tcFechaInicio",fecha);
            request.addProperty("tcFechaFinal",fecha);
            request.addProperty("tcNombre", "Gerald Morales");
            request.addProperty("tnSubNiveles", "N");
            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(url);
            transport.call(soapAction, soapEnvelope);

            resultadoXML = (SoapPrimitive) soapEnvelope.getResponse();

        } catch (Exception ex){

        }

    }
    public void onTextClicked (View view) throws IOException, XmlPullParserException {

        EditText entrada_textfield = findViewById(R.id.entradaTextView);

        String monedaConvertir = entrada_textfield.getText().toString();

        RadioButton dolarestocolones = findViewById(R.id.radioButtonD2C);
        RadioButton colonestodolares = findViewById(R.id.radioButtonC2D);

        InputMethodManager input_controlador = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        input_controlador.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(!monedaConvertir.isEmpty()){
            if(dolarestocolones.isChecked()){
                double valor_grados_numerico = Double.parseDouble(monedaConvertir) * valor_dolar;
                TextView salida_textview = findViewById(R.id.salidaTextView);
                String mostrar = String.format("Resultado: ₡%.4f", valor_grados_numerico);
                salida_textview.setText(mostrar);

            }else if(colonestodolares.isChecked()){
                double valor_grados_numerico = (Double.parseDouble(monedaConvertir)) / valor_dolar;
                TextView salida_textview = findViewById(R.id.salidaTextView);
                String mostrar = String.format("Resultado: $%.4f", valor_grados_numerico);
                salida_textview.setText(mostrar);

            }

        }



        //Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        salida_textview = findViewById(R.id.salidaTextView);
        valor_dolar = 0;
        SegundoPlano segundoPlanoTareas = new SegundoPlano();
        segundoPlanoTareas.execute();
    }

    private class SegundoPlano extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){

        }
        @Override
        protected  Void doInBackground(Void ... params){
            obtenerValorDolarWS();
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            valor_dolar = obtener_valor_dolar(resultadoXML.toString());
        }
    }
    public double obtener_valor_dolar(String texto){
        String resultado = "";

        Document documento = null;
        DocumentBuilder constructor_documento;
        DocumentBuilderFactory instancia_nueva;

        try {
            InputStream resultadoStream = new ByteArrayInputStream(texto.getBytes("UTF-8"));
            instancia_nueva = DocumentBuilderFactory.newInstance();
            constructor_documento = instancia_nueva.newDocumentBuilder();
            documento = constructor_documento.parse(resultadoStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(documento != null) {
            resultado = documento.getElementsByTagName("NUM_VALOR").item(0).getTextContent();
        }
        return Double.parseDouble(resultado);
    }

}
