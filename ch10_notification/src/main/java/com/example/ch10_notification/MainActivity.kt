package com.example.ch10_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.example.ch10_notification.databinding.ActivityMainBinding
import org.jetbrains.annotations.NonNls

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.notificationButton.setOnClickListener {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val builder: NotificationCompat.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelId="one-channel"
                val channelName="My Channel One"
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "My Channel One Description"
                    setShowBadge(true)
                    val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                    setSound(uri, audioAttributes)
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
                builder = NotificationCompat.Builder(this, channelId)
            }else {
                builder = NotificationCompat.Builder(this)
            }

            builder.run {
                setSmallIcon(R.drawable.small)
                setWhen(System.currentTimeMillis())
                setContentTitle("김바울")
                setContentText("안녕하세요ㅠ")
                setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.big))
            }

            val KEY_TEXT_REPLY = "key_text_reply"
            var replyLabel = "답장"
            var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
                setLabel(replyLabel)
                build()
            }
            val replyIntent = Intent(this, ReplyReceiver::class.java)
            val replyPendingIntent = PendingIntent.getBroadcast(
                this, 30, replyIntent, PendingIntent.FLAG_MUTABLE
            )

            builder.addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.send,
                    "답장",
                    replyPendingIntent
                ).addRemoteInput(remoteInput).build()
            )

            manager.notify(11, builder.build())

            val status1 = ContextCompat.checkSelfPermission(this,
            "android.permission.ACCESS_FINE_LOCATION")
            val status2 = ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_COARSE_LOCATION")
            if (status1 == PackageManager.PERMISSION_GRANTED &&  status2 == PackageManager.PERMISSION_GRANTED){
                Log.d("kkang", "permission granted")
            } else {
                Log.d("kkang", "permission denied")
            }



            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted){
                    Log.d("kkang", "callback, granted..")
            } else {
                    Log.d("kkang", "callback, denied..")
                }
            }

            requestPermissionLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
            requestPermissionLauncher.launch("android.permission.ACCESS_COURSE_LOCATION")
        }
    }
}