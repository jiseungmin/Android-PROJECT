package com.example.alarmapp

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onoff: Boolean
) {
    val timeText: String
        get(){ // 호출하여 값을 가져옴 return문 꼭 써줘야함 get과 set 개념알기
            val h = "%02d".format(if(hour<12)hour else hour-12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }
    val ampmText: String
    get() {
        return if (hour<12) "AM" else "PM"
    }
    val onofftext: String
    get() {
        return if (onoff) "알람 끄기" else "알람 켜기"
    }

    fun makeDataForDB():String{
        return "$hour:$minute"
    }

}
