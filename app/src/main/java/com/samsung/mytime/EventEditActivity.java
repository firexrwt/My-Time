package com.samsung.mytime;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class EventEditActivity extends AppCompatActivity{
    private EditText eventNameET, eventPriceET, eventEquipmentET;
    private TextView eventDateTV;
    private Button eventTimeButton;
    int hour, minute;
    String strTime;
    public static LocalTime time;
    public static String eventName;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:m");
    OpenHelper openHelper = new OpenHelper(this);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        createNotificationChannel();
        time = LocalTime.now();
        eventDateTV.setText(CalendarUtils.formattedDate(CalendarUtils.selectedDate));
    }

    private void initWidgets(){
        eventNameET = findViewById(R.id.eventNameET);
        eventPriceET = findViewById(R.id.eventPriceET);
        eventEquipmentET = findViewById(R.id.eventEquipmentET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeButton = findViewById(R.id.eventTimeButton);
    }
    public void popOutTimePicker(View view){
        eventTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfDay) {
                        Toast.makeText(EventEditActivity.this, ""+hourOfDay+":"+minuteOfDay, Toast.LENGTH_LONG).show();
                        strTime = ""+hourOfDay+":"+minuteOfDay;
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    private void createNotificationChannel(){
        CharSequence name = "MyTimeNotificationChannel";
        String description = "Channel for My Time Application";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("myTimeNotifications", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void saveEventAction(View view){
        eventName = eventNameET.getText().toString();
        String eventPrice = eventPriceET.getText().toString();
        String eventEquipment = eventEquipmentET.getText().toString();
        time = LocalTime.parse(strTime, timeFormatter);
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time, eventPrice, eventEquipment);
        Event.eventsList.add(newEvent);
        openHelper.insert(newEvent);
        Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EventEditActivity.this, EventReminder.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventEditActivity.this, 0, intent, 0);
        String dateTime = CalendarUtils.selectedDate.toString() + " " + time.toString();
        DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
        long timeAlarm = System.currentTimeMillis();
        long tenSecondsInMillis = 1000 * 10;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        try {
            Date date = dateTimeFormatter.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, -1);
            cal.set(Calendar.SECOND, 0);
            date = cal.getTime();
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finish();
    }
}