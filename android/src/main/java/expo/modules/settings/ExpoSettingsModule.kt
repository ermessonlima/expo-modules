

package expo.modules.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.settings.ml.Android
import org.tensorflow.lite.support.image.TensorImage
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Base64
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream


class ExpoSettingsModule : Module()  {

  private lateinit var model: Android
  lateinit var labels:List<String>
  var colors = listOf<Int>(
    Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
    Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    val paint = Paint()


  private val context: Context
    get() = appContext.reactContext ?: throw Exceptions.ReactContextLost()




  override fun definition() = ModuleDefinition {
    Name("ExpoSettings")





    Function("getTheme") { theme: String   ->
      try {

          labels = FileUtil.loadLabels(context, "labels.txt")

      if (!::model.isInitialized) {
        model = Android.newInstance(context)
      }

        val imageBitmap = base64ToBitmap(theme)
        val image = TensorImage.fromBitmap(imageBitmap)



        val outputs = model.process(image)

        val locations = outputs.locationAsTensorBuffer.floatArray
        val classes = outputs.categoryAsTensorBuffer.floatArray
        val scores = outputs.scoreAsTensorBuffer.floatArray




          var mutable = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
          val canvas = Canvas(mutable)


          val h = mutable.height
          val w = mutable.width
          paint.textSize = h/15f
          paint.strokeWidth = h/85f
          var x = 0


          scores.forEachIndexed { index, fl ->
              x = index
              x *= 4
              if(fl > 0.5){
                  paint.setColor(colors.get(index))
                  paint.style = Paint.Style.STROKE
                  canvas.drawRect(RectF(locations.get(x+1)*w, locations.get(x)*h, locations.get(x+3)*w, locations.get(x+2)*h), paint)
                  paint.style = Paint.Style.FILL
                  canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), locations.get(x+1)*w, locations.get(x)*h, paint)
              }
          }


          // Conversão do bitmap de volta para string base64
        val outputStream = ByteArrayOutputStream()
          mutable.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

        // Resolução da promessa com a imagem base64


        return@Function base64Image

    } catch (e: Exception) {
    // Em caso de erro, rejeita a promessa.
        return@Function e.message

  } finally {
    // Considera liberar os recursos do modelo se não for mais usado.
    // model.close()
//          model.close()
  }
    }


  }

   private fun base64ToBitmap(base64Str: String): Bitmap {
     val imageAsBytes = android.util.Base64.decode(base64Str.toByteArray(), android.util.Base64.DEFAULT)
     return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
   }



}