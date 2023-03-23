package com.example.conc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VentaActivity extends AppCompatActivity {

    ClsOpenHelper admin = new ClsOpenHelper(this, "concesionario.db", null, 1);
    EditText jetcodigo, jetidentificacion, jetfecha, jetplaca;
    CheckBox jcbactivo;
    String codigo, identificacion, fecha, placa;
    long respuesta;
    byte sw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        getSupportActionBar().hide();
        jetidentificacion = findViewById(R.id.etidentificacion);
        jetcodigo = findViewById(R.id.etcodigo);
        jetfecha = findViewById(R.id.etfecha);
        jetplaca = findViewById(R.id.etplaca);
        jcbactivo = findViewById(R.id.cbactivo);
        jetidentificacion.requestFocus();
        sw = 0;
    }


    public void Guardar(View view) {
        codigo = jetcodigo.getText().toString();
        fecha = jetfecha.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        placa = jetplaca.getText().toString();
        if (codigo.isEmpty() || fecha.isEmpty() || identificacion.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos para continuar", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = admin.getWritableDatabase(); // VERIFICANDO QUE EXISTAN TANTO ID COMO PLACA
            Cursor fila = db.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);
            if (fila.moveToNext()) {
                sw = 1;
                if (fila.getString(3).equals("Si")) {   // SI IDENTIFICACION ESTA ACTIVA VERIFICAMOS QUE PLACA TAMBIEN LO ESTE
                    Cursor filaPlaca = db.rawQuery("select * from TblVehiculo where Placa='" + placa + "'", null);
                    if (filaPlaca.moveToNext()) {
                        sw = 1;
                        if (filaPlaca.getString(3).equals("Si")) { // SI PLACA ESTA ACTIVA PROCEDEMOS A GUARDAR
                            SQLiteDatabase dbSave = admin.getWritableDatabase();
                            ContentValues registro = new ContentValues();
                            registro.put("codigo", codigo);
                            registro.put("fecha", fecha);
                            registro.put("Identificacion", identificacion);
                            registro.put("Placa", placa);
                            respuesta = dbSave.insert("TblVenta", null, registro);

                            if (respuesta > 0) {
                                Toast.makeText(this, "Venta Guardada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                            }
                            dbSave.close();
                        }
                    }
                } else {
                    Toast.makeText(this, "Identificacion no existe o no esta activa", Toast.LENGTH_SHORT).show();
                }
            }
            db.close();
        }
    }



    public void Consultar(View view){
        codigo=jetcodigo.getText().toString();
        if(codigo.isEmpty()){
            Toast.makeText(this, "Ingrese el codigo a consultar", Toast.LENGTH_SHORT).show();
        }else{
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila=db.rawQuery("select * from TblVenta where Codigo='"+codigo+"'",null);
            if(fila.moveToNext()){
                sw=1;
                if(fila.getString(3).equals("Si")){
                    jetfecha.setText(fila.getString(1));
                    jetidentificacion.setText(fila.getString(2));
                    jetplaca.setText(fila.getString(3));
                }else{
                    Toast.makeText(this, "Registro anulado, Activelo para consultar", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Venta no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }


    public void anular(View view){
        if(sw == 1){
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("activo","No");
            respuesta=db.update("TblVenta",registro,"codigo='"+codigo+"'",null);
            if(respuesta > 0){
                Toast.makeText(this, "Venta anulada", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "Error al anular la venta", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe consultar primero para aunular", Toast.LENGTH_SHORT).show();
        }
    }


    public void activar(View view){
        if(sw == 1){
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("activo","Si");
            respuesta=db.update("TblVenta",registro,"codigo='"+codigo+"'",null);
            if(respuesta > 0){
                Toast.makeText(this, "Venta anulada", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "Error al activar la venta", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe consultar primero para activar", Toast.LENGTH_SHORT).show();
        }
    }


    public void cancelar(View view){Limpiar_campos();}


    public void regresar(View view){
        Intent intMain=new Intent(this,MainActivity.class);
        startActivity(intMain);
    }


    private void Limpiar_campos(){
        jetcodigo.setText("");
        jetfecha.setText("");
        jetidentificacion.setText("");
        jetplaca.setText("");
        jcbactivo.setChecked(false);
        jetcodigo.requestFocus();
        sw=0;
    }






}