package com.example.padcx_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    val CHANNEL_ID = "PADCXNOTIFICATION_CHANNEL"
    val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        getFirebaseInstanceID()

       val  notificationId = 1996
       val  notificationId02 = 1997
       val  notificationId03 = 1998
       val  notificationId04 = 1999
       val  notificationId05 = 2000
       val  notificationId06 = 2001




        /***
         * Just Notification
         */
        val textTitle = "Hello Friend"
        val textContent = "I am a new Notification"

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line ..."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        btn_normalNoti.setOnClickListener {
            with(NotificationManagerCompat.from(this)){
                notify(notificationId,builder.build())
            }
        }


        /***
         * Notification with Intent
         */

        //create explicit intent
        val intent = SecondActivity.newIntent(this).apply {
            flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent : PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val builder2 = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        btn_notiWithIntent.setOnClickListener {
            with(NotificationManagerCompat.from(this)){
                notify(notificationId02,builder2.build())
            }
        }


        /***
         * Notification with Action
         */
        val snoozeIntent = SecondActivity.newIntent(this).apply {
            putExtra(EXTRA_NOTIFICATION_ID,0)
        }
        val snoozePendingIntent : PendingIntent = PendingIntent.getActivity(this,0,snoozeIntent,0)
        val builder3 = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.star_off,"Snooze",snoozePendingIntent)

        btn_notiWithAction.setOnClickListener {
            with(NotificationManagerCompat.from(this)){
                notify(notificationId03,builder3.build())
            }
        }


        /***
         * Notification with Progress
         */
        val builder4 = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Picture Download")
            setContentText("Download in progress")
            setSmallIcon(android.R.drawable.stat_sys_download)
        }
        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 10
        NotificationManagerCompat.from(this).apply {
            // Issue the initial notification with zero progress
            builder4.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)

            // Do the job here that tracks the progress.
            // Usually, this should be in a
            // worker thread
            // To show progress, update PROGRESS_CURRENT and update the notification with:
            // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            // notificationManager.notify(notificationId, builder.build());

            // When done, update the notification one more time to remove the progress bar
            builder.setContentText("Download complete")
                .setProgress(0, 0, false)

            btn_notiWithProgress.setOnClickListener {
                notify(notificationId04, builder4.build())
            }
        }



        /***
         * Notification with Image
         */

        //Get drawable from resource
        val drawable : Drawable? = ContextCompat.getDrawable(this,R.drawable.ic_launcher_background)

        //Convert drawable to bitmap
        val bitmap : Bitmap? = drawable?.toBitmap()

        val builder5 = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("ImageTitle")
            .setContentText("ImageDescription")
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigLargeIcon(bitmap)
                .bigPicture(bitmap))

        btn_notiWithImage.setOnClickListener {
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId05, builder5.build())
            }
        }


        /***
         * Custom Notification
         */

        val notificationLayout = RemoteViews(packageName,R.layout.layout_notification)

        val builder6 = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            //.setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        btn_customNoti.setOnClickListener {
            with(NotificationManagerCompat.from(this)){
                notify(notificationId06,builder6.build())
            }
        }

    }

    private fun getFirebaseInstanceID() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TOKEN", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = "token: $token"
                Log.d("TOKEN", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

    private fun createNotificationChannel() {
      //Create notification channel, but only on API 26+ because
        //Notification class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val name = "PADCX"
            val descriptionText = "PADC Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description=descriptionText
            }

            //register the channel with the system
            val notificationManager : NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}