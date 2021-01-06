package com.seru.serujuragan.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.os.Environment
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.telephony.PhoneNumberUtils
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import com.seru.serujuragan.App
import com.seru.serujuragan.R
import com.seru.serujuragan.config.AppPreference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object AndroidUtils {

    const val deviceType = "ANDROID"
    private val displaySize = Point()
    private val displayMetrics = DisplayMetrics()
    var density = 1F
    var resourceType = "unknown"
    var statusBarHeight: Int = 0
    val displayWidth: Int get() { return displaySize.x }
    val displayHeigt: Int get() { return displaySize.y }
    val displayWidthPixels: Int get() { return displayMetrics.widthPixels}
    val displayHeightPixels: Int get() { return displayMetrics.heightPixels}

    var maxWidthChat = 0F

    fun checkDisplay(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        display.getSize(displaySize)
        display.getMetrics(displayMetrics)

        density = context.resources.displayMetrics.density
        resourceType = when (context.resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "ldpi"
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_260,
            DisplayMetrics.DENSITY_280,
            DisplayMetrics.DENSITY_300,
            DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            DisplayMetrics.DENSITY_340,
            DisplayMetrics.DENSITY_360,
            DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420,
            DisplayMetrics.DENSITY_440,
            DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            DisplayMetrics.DENSITY_560,
            DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            else -> "unknown"
        }

        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0)
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)

        val viewItemPadding = context.resources.getDimension(R.dimen.padding_5dp)
        val viewChatPadding = context.resources.getDimension(R.dimen.padding_7dp)
        val viewMessagePadding = context.resources.getDimension(R.dimen.padding_5dp)
        val spaceSize = context.resources.getDimension(R.dimen.margin_12dp)
        val timestampSize = context.resources.getDimension(R.dimen.size_view_45dp) + (6*AndroidUtils.density)
        val dotSize = (8*AndroidUtils.density) + (context.resources.getDimension(R.dimen.margin_2dp)*2)
        maxWidthChat = AndroidUtils.displayWidth - (viewItemPadding*2) - (viewChatPadding*2) - (viewMessagePadding*2) - spaceSize - timestampSize -dotSize
    }

    fun getStringBytes(src: String): ByteArray {
        try {
            return src.toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    fun resolveChatText(_texts: Array<String>, _paint: Paint, maxWidth: Int): String {
        var formattedText = ""
        var workingText = ""
        for (section in _texts) {
            val newPart = (if (workingText.isNotEmpty()) " " else "") + section
            workingText += newPart
            val width = _paint.measureText(workingText, 0, workingText.length)
            if (width > maxWidth) {
                formattedText += (if (formattedText.isNotEmpty()) "\n" else "") +
                        workingText.substring(0, workingText.length - newPart.length)
                workingText = section
            }
        }
        if (workingText.isNotEmpty())
            formattedText += (if (formattedText.isNotEmpty()) "\n" else "") + workingText
        return formattedText
    }

    val text10 = "00000000"

    fun adjustTextSize(paint: Paint, numCharacters: Int, widthPixels: Int): Float {
        val width = paint.measureText(text10)*numCharacters/text10.length
        return (widthPixels/width) * paint.textSize

        // remeasure with font size near our desired result
//        width = paint.measureText(text10)*numCharacters/text10.length
//        newSize = (widthPixels/width)*paint.textSize
//        paint.textSize = newSize

//        // Check height constraints
//        val metrics = paint.fontMetricsInt
//        val textHeight = metrics.descent-metrics.ascent
//        if (textHeight > heightPixels) {
//            newSize *= (heightPixels/textHeight)
//            paint.textSize = newSize
//        }

//        return paint
    }

    const val TIMER_BLOCK_START = "("
    const val TIMER_BLOCK_END = ")"
    const val TIMER_SEPARATOR = "."
    const val TIMER_ZERO = "0"

    /* Folder Utils */

    val BASE_DIR_INTERNAL = App.getContext().applicationInfo.dataDir
    val BASE_DIR_EXTERNAL = Environment.getExternalStorageDirectory().absolutePath
    val BASE_DIR_EXTERNAL_CACHE = App.getContext().externalCacheDir?.absolutePath
    val BASE_DIR_EXTERNAL_FILE = App.getContext().getExternalFilesDir(null)?.absolutePath

    const val BASE_DIR_IMAGE = "/Ngobrol/Media/Ngobrol Images"
    const val BASE_DIR_VIDEO = "/Ngobrol/Media/Ngobrol Videos"
    const val BASE_DIR_AUDIO = "/Ngobrol/Media/Ngobrol Audios"
    const val BASE_DIR_DOCUMENT = "/Ngobrol/Media/Ngobrol Documents"
    const val BASE_DIR_SENT = "/Sent"

    val DIR_MEDIA_THUMB = "$BASE_DIR_INTERNAL/app_media/thumb"

    val DIR_EXTERNAL_USER = "$BASE_DIR_EXTERNAL_FILE/user"
    val DIR_IMAGES = BASE_DIR_EXTERNAL + BASE_DIR_IMAGE
    val DIR_IMAGES_SENT = DIR_IMAGES + BASE_DIR_SENT
    val DIR_VIDEOS = BASE_DIR_EXTERNAL + BASE_DIR_VIDEO
    val DIR_VIDEOS_SENT = DIR_VIDEOS + BASE_DIR_SENT
    val DIR_AUDIOS = BASE_DIR_EXTERNAL + BASE_DIR_AUDIO
    val DIR_AUDIOS_SENT = DIR_AUDIOS + BASE_DIR_SENT
    val DIR_DOCUMENTS = BASE_DIR_EXTERNAL + BASE_DIR_DOCUMENT
    val DIR_DOCUMENTS_SENT = DIR_DOCUMENTS + BASE_DIR_SENT
    val DIR_TEMP_ATTACHMENT = "$BASE_DIR_EXTERNAL_CACHE/attachment"
    val DIR_IMAGE_TEMP = "$BASE_DIR_EXTERNAL_CACHE/attachment/tempImage"
    val DIR_IMAGE_EDITED = "$BASE_DIR_EXTERNAL_CACHE/attachment/editedImage"
    val DIR_USER_AVATAR = "$DIR_EXTERNAL_USER/avatar"
    val DIR_USER_AVATAR_THUMB = "$DIR_USER_AVATAR/thumb"
    val DIR_USER_STICKER = "$DIR_EXTERNAL_USER/stc"
    val DIR_USER_STICKER_THUMB = "$DIR_USER_STICKER/thumb"

    const val SMALL_AVATAR = "small"
    const val LARGE_AVATAR = "large"

    fun createMediaFolder() {
        Thread {
            val paths = arrayOf(DIR_IMAGES_SENT, DIR_VIDEOS_SENT, DIR_AUDIOS_SENT, DIR_DOCUMENTS_SENT)
            for (path in paths) {
                val dir = File(path)
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        val fileOutputStream: FileOutputStream
                        try {
                            fileOutputStream = FileOutputStream("$dir/.nomedia")
                            fileOutputStream.flush()
                            fileOutputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    val fileOutputStream: FileOutputStream
                    try {
                        fileOutputStream = FileOutputStream("$dir/.nomedia")
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    fun deleteFile(filePath: String): Boolean { return File(filePath).delete() }

    fun deleteFilesFromDir(folderPath: String) {
        val file = File(folderPath)
        if (file.isDirectory)
            for (child in file.listFiles())
                child.delete()
    }

    enum class Response {
        SUCCESS {
            override fun toString(): String {
                return "success"
            }
        },
        FAILED {
            override fun toString(): String {
                return "failed"
            }
        },
        ERROR {
            override fun toString(): String {
                return "error"
            }
        }
    }

    val resendCodeTimer = intArrayOf(30000, 60000, 600000) //In sec.

//    fun formatPhone(phone: String): String {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            PhoneNumberUtils.formatNumber("+$phone", LocaleManager.LANG_ENGLISH)
//        else "+" + PhoneNumberUtils.formatNumber(phone)
//    }

    fun formatPhoneStartZero(phone: String): String {
        return if (phone.startsWith("62")) {
            val formatted = phone.replaceFirst("62".toRegex(), "0")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                PhoneNumberUtils.formatNumber(formatted, "US")
            else PhoneNumberUtils.formatNumber(formatted)
        } else PhoneNumberUtils.formatNumber(phone)
    }

    fun getStringTimer(time: Long): String {
        val minute = time/60000
        val second = time%60000/1000
        val mins = minute.toString()
        val secs = if (second<10) "0$second" else second.toString()
        return mins + TIMER_SEPARATOR + secs
    }

    fun getStringDuration(time: Long): String {
        val hour = (time/(1000*60*60)) % 24
        val minute = (time/60000) % 60
        val second = time%60000/1000
        val hrs = if (hour>0) if (hour < 10) "0$hour$TIMER_SEPARATOR" else "$hour$TIMER_SEPARATOR" else ""
        val mins = if (minute < 10) "0$minute" else minute.toString()
        val secs = if (second < 10) "0$second" else second.toString()
        return "$hrs$mins$TIMER_SEPARATOR$secs"
    }

    fun formatStatus(status: String): String { return "'' $status ''" }

    /* Image Utils */

    @SuppressLint("NewApi")
    fun imageBlur(context: Context, sentBitmap: Bitmap, radius: Int): Bitmap? {

        if (Build.VERSION.SDK_INT > 19) {
            val bitmap = sentBitmap.copy(sentBitmap.config, true)
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(
                rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            script.setRadius(radius.toFloat() /* e.g. 3.f */)
            script.setInput(input)
            script.forEach(output)
            output.copyTo(bitmap)
            return bitmap
        }

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        val bitmap = sentBitmap.copy(sentBitmap.config, true)

        if (radius < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)

        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    /* Date Utils */

    private const val DATE_ID = "dd MMMM yyyy"
    private const val DATE_US = "MMMM dd, yyyy"
    private const val DATE_ID_2 = "dd MMM yyyy"
    private const val DATE_US_2 = "MMM dd, yyyy"
    private const val DATE_YMD = "yyyy-MM-dd"
    private const val DATE_SLASH = "dd/MM/yyyy"
    private const val TIME_24H = "HH:mm"
    private const val TIME_APM = "hh:mm a"
    private const val TIME_HMS = "HH:mm:ss"
    private const val DATETIMESTAMP = "yyyyMMddHHmmss"

//    val DATE_ONLY_ID: DateFormat = SimpleDateFormat(DATE_ID, LocaleManager.getLocale(App.getContext()))
//    val DATE_ONLY_US: DateFormat = SimpleDateFormat(DATE_US, LocaleManager.getLocale(App.getContext()))
//    val DATE_ONLY_ID_2: DateFormat = SimpleDateFormat(DATE_ID_2, LocaleManager.getLocale(App.getContext()))
//    val DATE_ONLY_US_2: DateFormat = SimpleDateFormat(DATE_US_2, LocaleManager.getLocale(App.getContext()))
//    val TIME_ONLY_24H: DateFormat = SimpleDateFormat(TIME_24H, LocaleManager.getLocale(App.getContext()))
//    val TIME_ONLY_AMPM: DateFormat = SimpleDateFormat(TIME_APM, LocaleManager.getLocale(App.getContext()))
//    val DATE_ONLY_YMD: DateFormat = SimpleDateFormat(DATE_YMD, LocaleManager.getLocale(App.getContext()))
//    val DATE_ONLY_SLASH: DateFormat = SimpleDateFormat(DATE_SLASH, LocaleManager.getLocale(App.getContext()))
//    val TIME_ONLY_HMS: DateFormat = SimpleDateFormat(TIME_HMS, LocaleManager.getLocale(App.getContext()))
//    val DATE_AND_TIME_FULL: DateFormat = SimpleDateFormat("$DATE_YMD, $TIME_HMS", LocaleManager.getLocale(App.getContext()))
//    val DATE_AND_TIME_FULL_2: DateFormat = SimpleDateFormat("$DATE_YMD, $TIME_APM", LocaleManager.getLocale(App.getContext()))
//    val DATE_TIMESTAMP: DateFormat = SimpleDateFormat(DATETIMESTAMP, LocaleManager.getLocale(App.getContext()))
//
//    fun getLocaleDate(): DateFormat {
//        return if (AppPreference.appLanguage == LocaleManager.LANG_BAHASA)
//            DATE_ONLY_ID
//        else DATE_ONLY_US
//    }
//
//    fun getLocaleDate2(): DateFormat {
//        return if (AppPreference.appLanguage == LocaleManager.LANG_BAHASA)
//            DATE_ONLY_ID_2
//        else DATE_ONLY_US_2
//    }

//    fun getStringTimestamp(time: Long): String {
//        val today = App.getContext().getString(R.string.today)
//        val yesterday = App.getContext().getString(R.string.yesterday)
//        val date = getLocaleDate2().format(Date().time)
//        try {
//            val dateSentCal = Calendar.getInstance()
//            val dateSent = Date(time)
//            dateSentCal.time = dateSent
//            val year = dateSentCal.get(Calendar.YEAR)
//            val month = dateSentCal.get(Calendar.MONTH)
//            val day = dateSentCal.get(Calendar.DAY_OF_MONTH)
//
//            val dateNowCal = Calendar.getInstance()
//            val dateNow = Date()
//            dateNowCal.time = dateNow
//            val year2 = dateNowCal.get(Calendar.YEAR)
//            val month2 = dateNowCal.get(Calendar.MONTH)
//            val day2 = dateNowCal.get(Calendar.DAY_OF_MONTH)
//
//            val diffDate = dateSent.compareTo(dateNow)
//            return if (diffDate < 0)
//                if (year == year2 && month == month2)
//                    when {
//                        day == day2 -> today
//                        day2 - day == 1 -> yesterday
//                        else -> date
//                    }
//                else date
//            else if (diffDate == 0)
//                today
//            else date
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return date
//        }
//
//    }
//
//    fun diffDate(d0: Long, d1: Long): Boolean {
//        return DATE_ONLY_SLASH.format(Date(d0))!= DATE_ONLY_SLASH.format(Date(d1))
//    }

}
