package mx.tecnm.tepic.mghs.tarea1_u4

import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val siLecturaCalendario = 1
    val siEdicionCalendario = 2
    var añoSeleccionado = 0
    var mesSeleccionado = 0
    var diaSeleccionado = 0
    var calSeleccionado:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALENDAR)
                ,siLecturaCalendario)}

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_CALENDAR)
                ,siEdicionCalendario)}

        calSeleccionado = consultarCalendario()


        Calendario.setOnDateChangeListener { view, año, mes, dia ->
            contenedorFecha.setText((mes+1).toString()+"/"+dia+"/"+año)
            añoSeleccionado=año
            mesSeleccionado=mes+1
            diaSeleccionado=dia
        }

        btnCrearEvento.setOnClickListener{
            crearEvento()
            editNombreEvento.setText("")
            hInicio.setSelection(0)
            mInicio.setSelection(0)
            hCierre.setSelection(0)
            mCierre.setSelection(0)
            contenedorFecha.setText("FECHA DEL EVENTO")
        }



    }

    private fun crearEvento() {
        val startMillis: Long = Calendar.getInstance().run {
            set(añoSeleccionado,
                mesSeleccionado,
                diaSeleccionado,
                hInicio.selectedItem.toString().toInt(),
                mInicio.selectedItem.toString().toInt()
            )
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(añoSeleccionado,
                mesSeleccionado,
                diaSeleccionado,
                hCierre.selectedItem.toString().toInt(),
                mCierre.selectedItem.toString().toInt()
            )
            timeInMillis
        }

        val values = ContentValues().apply{
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, editNombreEvento.text.toString())
            put(CalendarContract.Events.CALENDAR_ID, calSeleccionado)
            put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
        }

        val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
        val eventID: Long = uri.lastPathSegment!!.toLong()
        Toast.makeText(this,"URI DEL EVENTO: ${uri}", Toast.LENGTH_LONG).show()
    }

    private fun consultarCalendario(): Long {
        var aRegresar:Long = 0

        val proyeccion_evento : Array<String> = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
        )

        val INDEX_ID: Int = 0
        val INDEX_USUARIO: Int = 1
        val ID_NOMBRE_DISPLAY: Int = 2
        val INDEX_DUEÑO: Int = 3

        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        val selection: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND ("+
                "${CalendarContract.Calendars.ACCOUNT_TYPE} =?) AND ("+
                "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
        val selectionArgs: Array<String> = arrayOf("meteorickestrel@gmail.com","com.gmail","meteorickestrel@gmail.com")

        val cu : Cursor =
            contentResolver.query(uri,proyeccion_evento, selection, selectionArgs, null)!!

        while(cu.moveToNext()){
            val calID: Long = cu.getLong(INDEX_ID)
            val nomDisp: String = cu.getString(ID_NOMBRE_DISPLAY)

            aRegresar = calID

        }
        Toast.makeText(this,"EL ID DEL CALENDARIO ES: ${aRegresar}", Toast.LENGTH_LONG).show()
        return aRegresar
    }
}