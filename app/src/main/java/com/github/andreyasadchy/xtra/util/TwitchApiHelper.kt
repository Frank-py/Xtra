package com.github.andreyasadchy.xtra.util

import android.content.Context
import android.text.format.DateUtils
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.model.chat.GlobalBadgesResponse
import com.github.andreyasadchy.xtra.util.chat.LiveChatThread
import com.github.andreyasadchy.xtra.util.chat.MessageListenerImpl
import com.github.andreyasadchy.xtra.util.chat.OnChatMessageReceivedListener
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.*


object TwitchApiHelper {

    var checkedValidation = false

    fun getTemplateUrl(url: String, size: String, game: Boolean = false, video: Boolean = false): String {
        if (url == "") return if (game) "https://static-cdn.jtvnw.net/ttv-static/404_boxart.jpg" else
            "https://vod-secure.twitch.tv/_404/404_processing_320x180.png"
        val width = if (game) {when (size) {"large" -> "272" "medium" -> "136" else -> "52"} } else {
            when (size) {"large" -> "640" "medium" -> "320" else -> "80"} }
        val height = if (game) {when (size) {"large" -> "380" "medium" -> "190" else -> "72"} } else {
            when (size) {"large" -> "360" "medium" -> "180" else -> "45"} }
        return if (video) url.replace("%{width}", width).replace("%{height}", height) else
            url.replace("{width}", width).replace("{height}", height)
    }

    fun getDuration(duration: String): Long {
        return try {
            parseDouble(duration)
            duration.toLong()
        } catch (e: NumberFormatException) {
            val h = duration.substringBefore("h", "0").takeLast(2).filter { it.isDigit() }.toInt()
            val m = duration.substringBefore("m", "0").takeLast(2).filter { it.isDigit() }.toInt()
            val s = duration.substringBefore("s", "0").takeLast(2).filter { it.isDigit() }.toInt()
            ((h * 3600) + (m * 60) + s).toLong()
        }
    }

    fun parseIso8601Date(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(date)?.time ?: 0L
    }

    fun formatTime(context: Context, iso8601date: String): String {
        return formatTime(context, parseIso8601Date(iso8601date))
    }

    fun formatTime(context: Context, date: Long): String {
        val year = Calendar.getInstance().let {
            it.timeInMillis = date
            it.get(Calendar.YEAR)
        }
        val format = if (year == Calendar.getInstance().get(Calendar.YEAR)) {
            DateUtils.FORMAT_NO_YEAR
        } else {
            DateUtils.FORMAT_SHOW_DATE
        }
        return DateUtils.formatDateTime(context, date, format)
    }

    fun startChat(channelName: String, userName: String?, userToken: String?, globalBadges: GlobalBadgesResponse?, channelBadges: GlobalBadgesResponse?, newMessageListener: OnChatMessageReceivedListener): LiveChatThread {
        return LiveChatThread(userName, userToken, channelName, MessageListenerImpl(globalBadges, channelBadges, newMessageListener)).apply { start() }
    }

    fun parseClipOffset(url: String): Double {
        val time = url.substringAfterLast('=').split("\\D".toRegex())
        var offset = 0.0
        var multiplier = 1.0
        for (i in time.lastIndex - 1 downTo 0) {
            offset += time[i].toDouble() * multiplier
            multiplier *= 60
        }
        return offset
    }

    fun formatViewsCount(context: Context, count: Int, viewcount: Boolean): String {
        return if (count > 1000 && viewcount) {
            context.getString(R.string.views, formatCountIfMoreThanAThousand(count))
        } else {
            context.resources.getQuantityString(R.plurals.views, count, count)
        }
    }

    fun formatViewersCount(context: Context, count: Int, viewcount: Boolean): String {
        return if (count > 1000 && viewcount) {
            context.getString(R.string.viewers, formatCountIfMoreThanAThousand(count))
        } else {
            context.resources.getQuantityString(R.plurals.viewers, count, count)
        }
    }

    fun formatCount(count: Int, viewcount: Boolean): String {
        return if (count > 1000 && viewcount) {
            formatCountIfMoreThanAThousand(count)
        } else {
            count.toString()
        }
    }

    fun addTokenPrefix(token: String) = "Bearer $token"

    private fun formatCountIfMoreThanAThousand(count: Int): String {
        val divider: Int
        val suffix = if (count.toString().length < 7) {
            divider = 1000
            "K"
        } else {
            divider = 1_000_000
            "M"
        }
        val truncated = count / (divider / 10)
        val hasDecimal = truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) "${truncated / 10.0}$suffix" else "${truncated / 10}$suffix"
    }
}