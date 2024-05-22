package pt.ulisboa.tecnico.cmov.pharmacist.util

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class UtilFunctions {
    companion object{
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap =
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun Int.dpToPx(resources : Resources): Int = (this * resources.displayMetrics.density).toInt()

        fun saveToInternalStorage(bitmapImage: Bitmap, imageFilename : String, activity: Activity): String {
            val cw = ContextWrapper(activity.applicationContext)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val myPath = File(directory, imageFilename)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(myPath)
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return directory.absolutePath
        }

//        private fun loadImageFromStorage(path: String, filename : String, activity: Activity) {
//            try {
//                val f = File(path, filename)
//                val b = BitmapFactory.decodeStream(FileInputStream(f))
//                val img: ImageView = activity.findViewById(R.id.imgPicker)
//                img.setImageBitmap(b)
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//        }
    }

}