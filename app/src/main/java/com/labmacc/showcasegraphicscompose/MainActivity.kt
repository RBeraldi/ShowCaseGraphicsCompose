package com.labmacc.showcasegraphicscompose

import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.core.graphics.withMatrix
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core.flip
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.INTER_NEAREST
import org.opencv.imgproc.Imgproc.resize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (OpenCVLoader.initDebug()){
            Toast.makeText(this,"openCV Loaded. Current version is: "+ OpenCVLoader.OPENCV_VERSION,Toast.LENGTH_SHORT).show()
        }

        val supermario = ImageBitmap.imageResource(resources, R.drawable.supermario )
        setContent {
        //    Perspective("Spazio 1999")
        //    ComposeCanvas(id = supermario)
        //      NativeCanvas(supermario)
            ImageProcessor(supermario.asAndroidBitmap())
                }
    }
}

@Composable
fun ImageProcessor(id: Bitmap){
    //A simple example of image procenssing with openCV
    val mm= Mat()
    val mm2 = Mat()
    Utils.bitmapToMat(id,mm)
    flip(mm,mm,-1)//f
    //flip code 0 means flipping around the x-axis
    // positive value means flipping around y-axis.
    // Negative value means flipping around both axes.
    val w=2000.0;val h=2000.0
    resize(mm,mm2,Size(w,h),w,h, INTER_NEAREST)
    val processedBitmap = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)

    //Utils.matToBitmap(mm, id)

    Utils.matToBitmap(mm2,processedBitmap)
    Canvas(modifier = Modifier.fillMaxSize(),
        onDraw = {
            drawIntoCanvas {
               // it.drawImage(id.asImageBitmap(), Offset(0f,0f),Paint())
                it.drawImage(processedBitmap.asImageBitmap(), Offset(0f,0f),Paint())
            }
        })
}

@Composable
fun NativeCanvas(id: ImageBitmap){
     Canvas(modifier = Modifier.fillMaxSize(),
            onDraw = {
                drawIntoCanvas {
                    val matrix = Matrix()
                    //Camera().rotateX(87f)
                    it.nativeCanvas.withMatrix(matrix) {
                        drawBitmap(id.asAndroidBitmap(),0f,0f,null)
                    }
                }
            } )
       }

@Composable
fun ComposeCanvas(id: ImageBitmap){
        Canvas(modifier = Modifier.fillMaxSize(),
            onDraw = {
                drawIntoCanvas {
                    it.rotate(15f)
                    it.scale(0.93f,0.9f)
                    it.translate(45f,56f)
                    it.skew(0.4f,0.7f)
                    it.drawImage(id, Offset(0f,0f),Paint())
                }
            } )
}

@Composable
fun Perspective(text: String){
    //Set the height in pixels of the text
    val tHeight = 200f
    val tHeightPerspective = 60f //height after perspective
    val mPaint = android.graphics.Paint().apply {
        textSize=tHeight
    }
    val tLength = mPaint.measureText(text, 0, text.length)

    //Set the four points to map
    val x0=0f; val y0=0f
    val x1=tLength; val y1=0f
    val x2=tLength; val y2=tHeight
    val x3=0f; val y3=tHeight


    val src = floatArrayOf(
        x0, y0, // top left point
        x1, y1, // top right point
        x2, y2, // bottom right point
        x3, y3 // bottom left point
    )
    val dst = floatArrayOf(
        x0, y0, // top left point
        x1, tHeight-tHeightPerspective, // top right point
        x2, y2, // bottom right point
        x3, y3 // bottom left point
    )

    val matrix = Matrix().apply {
        setPolyToPoly(src, 0, dst, 0, 4)
    }

    Canvas(modifier = Modifier.fillMaxSize(),
            onDraw = {
                drawIntoCanvas {
                    it.nativeCanvas.withMatrix(matrix) {
                        drawText(text,0f,tHeight,mPaint)
                        drawCircle(x0,y0,10f,mPaint)
                        drawCircle(x1,y1,10f,mPaint)
                        drawCircle(x2,y2,10f,mPaint)
                        drawCircle(x3,y3,10f,mPaint)
                        drawLine(x0,y0,x1,y1,mPaint)
                        drawLine(x1,y1,x2,y2,mPaint)
                        drawLine(x2,y2,x3,y3,mPaint)
                        drawLine(x3,y3,x0,y0,mPaint)
                    }
                }
            } )
    }