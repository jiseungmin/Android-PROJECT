package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() { // firebasemessaginservice를 상속받는다.

    override fun onNewToken(token: String) { // 만약 토큰값이 바뀔수있을때를 대비하여 새로운토큰이 갱신할떄 서버에 갱신한 값을 주는 override
        super.onNewToken(token)
    }

    override fun onMessageReceived(remotemessage: RemoteMessage) { // 메세지를 받을때 이 함수 실행
        super.onMessageReceived(remotemessage)

        createNotificationChannel()

        val type = remotemessage.data["type"]
            ?.let { NotipicationType.valueOf(it) } // type
        val title = remotemessage.data["title"] // firebase에서 오는 데이터 받기
        val message = remotemessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this) // 빌드 실행
            .notify(type.id, createNotification(type, title, message))
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(
        type: NotipicationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // 기존 액티비티 갱신 찾아보자 addflags
        }
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_MUTABLE)
        val notificationBulider = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24) //  알림띄우기
            .setContentTitle(title) // 알림 제목
            .setContentText(message) // 알림 메세지 띄우기
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 버전이 priority 지정해줘야함
            .setContentIntent(pendingIntent) // intent
            .setAutoCancel(true) // 이전메세지를 지워줌

        when (type) {
            NotipicationType.NORMAL -> Unit
            NotipicationType.EXPANDABLE -> { // type이 확장형알림  //큰텍스트블록 생성
                notificationBulider.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "\uD83D\uDE00 \uD83D\uDE03 \uD83D\uDE04 \uD83D\uDE01 \uD83D\uDE06 \uD83D\uDE05 \uD83D\uDE02 \uD83E\uDD23 \uD83D\uDE07 \uD83D\uDE09 \uD83D\uDE0A \uD83D\uDE42 \uD83D\uDE43 ☺ \uD83D\uDE0B \uD83D\uDE0C \uD83D\uDE0D \uD83E\uDD70 \uD83D\uDE18 \uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE1A \uD83E\uDD72 \uD83E\uDD2A \uD83D\uDE1C \uD83D\uDE1D \uD83D\uDE1B \uD83E\uDD11 \uD83D\uDE0E \uD83E\uDD13 \uD83E\uDD78 \uD83E\uDDD0 \uD83E\uDD20 \uD83E\uDD73 \uD83E\uDD21 \uD83D\uDE0F "
                        )
                )
            }
            NotipicationType.CUSTOM -> { //커스텀알림 만들기
                notificationBulider
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom
                        ).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )

            }
        }
        return notificationBulider.build()
    }

    private fun createNotificationChannel() { // 알림을 받기 위한 채널 만들기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // 중요도 알림울리기
            )
            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel) //채널생성
        }
    }


    companion object {
        private const val CHANNEL_NAME = "EMOJI Party"
        private const val CHANNEL_DESCRIPTION = "EMOJI Party를 위한 채널"
        private const val CHANNEL_ID = "Channel ID"
    }
}