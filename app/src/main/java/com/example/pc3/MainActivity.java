package com.example.pc3;
import static com.example.pc3.R.id.listView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pc3.model.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button bfecha,bhora;
    EditText efecha,ehora,evalor;

    private int dia,mes,anio,hora,minutos;

    ListView list_datos;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Data>listaData=new ArrayList<Data>();
    ArrayAdapter<Data>arrayAdapterData;
    Data dataSeleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bfecha=(Button) findViewById(R.id.btnFecha);
        bhora=(Button) findViewById(R.id.btnHora);

        efecha=(EditText) findViewById(R.id.eFecha);
        ehora=(EditText) findViewById(R.id.eHora);
        evalor=(EditText)findViewById(R.id.eValor);

        bfecha.setOnClickListener(this);
        bhora.setOnClickListener(this);

        list_datos=findViewById(listView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializarFireBase();

        listaDatos();
        list_datos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataSeleccionada=(Data) parent.getItemAtPosition(position);

                String fechaHora = dataSeleccionada.getFechaHora();
                String[] parts = fechaHora.split(" ");
                String afecha = parts[0];
                String hora = parts[1];
                efecha.setText(afecha);
                ehora.setText(hora);
                evalor.setText(dataSeleccionada.getValor());
            }
        });
    }



    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    private void listaDatos() {
        databaseReference.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaData.clear();
                for (DataSnapshot objSnaptshot : snapshot.getChildren()){
                    Data d=objSnaptshot.getValue(Data.class);
                    listaData.add(d);

                    arrayAdapterData=new ArrayAdapter<Data>(MainActivity.this, android.R.layout.simple_list_item_1,listaData);
                    list_datos.setAdapter(arrayAdapterData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==bfecha){
            final Calendar c= Calendar.getInstance();
            dia=c.get(Calendar.DAY_OF_MONTH);
            mes=c.get(Calendar.MONTH);
            anio=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    efecha.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                }
            }
            ,dia,mes,anio);
            datePickerDialog.show();
        }
        if(v==bhora){
            final Calendar c= Calendar.getInstance();
            hora=c.get(Calendar.HOUR_OF_DAY);
            minutos=c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    ehora.setText(hourOfDay+":"+minute);
                }
            },hora,minutos,false);
            timePickerDialog.show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String fecha=efecha.getText().toString();
        String hora=ehora.getText().toString();
        String valor=evalor.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:
                if(fecha.equals("")||hora.equals("")||valor.equals("")){
                    validacion();
                }else {
                    String fechaHora = fecha + " " + hora;
                    Data d=new Data();
                    d.setIdsensor(UUID.randomUUID().toString());
                    d.setFechaHora(fechaHora);
                    d.setValor(valor);
                    databaseReference.child("Data").child(d.getIdsensor()).setValue(d);

                    Toast.makeText(this,"Agregar", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;
            case R.id.icon_save:
                Data d=new Data();
                d.setIdsensor(dataSeleccionada.getIdsensor());
                String fechaHora = fecha + " " + hora;
                d.setFechaHora(fechaHora);
                d.setValor(evalor.getText().toString());
                databaseReference.child("Data").child(d.getIdsensor()).setValue(d);
                Toast.makeText(this,"Guardar", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            case R.id.icon_delete:
                Data de=new Data();
                de.setIdsensor(dataSeleccionada.getIdsensor());
                databaseReference.child("Data").child(de.getIdsensor()).removeValue();
                Toast.makeText(this,"Eliminar", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;

            default:break;
        }
        return true;
    }

    private void limpiarCajas() {
        efecha.setText("");
        ehora.setText("");
        evalor.setText("");
    }

    private void validacion() {
        String fecha=efecha.getText().toString();
        String hora=ehora.getText().toString();
        String valor=evalor.getText().toString();
        if(fecha.equals("")){
            efecha.setError("Requerido");
        }else if(hora.equals("")){
            ehora.setError("Requerido");
        }else if(valor.equals("")){
            evalor.setError("Requerido");
        }
    }
}