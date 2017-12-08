package com.today36524.logrf

import java.io.{FileNotFoundException, RandomAccessFile}

import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {

    //初级测试
    /*try{
      val file = Source.fromFile("E:/detail-productdb-service.2017-11-29.log")
      //throw new Exception
      for(line <- file.getLines.take(10)) println(line)
      println(file.size)
      file.close()
    }catch{
      case ex:FileNotFoundException =>
        println("抛出一个异常："+ex.getMessage)
        ex.printStackTrace
      case _ =>
        println("默认抛出")
    }*/

    //使用java中的随机读写文件RandomAccessFile测试
    val raf:RandomAccessFile =
      new RandomAccessFile("E:/detail-productdb-service.2017-11-29.log","r")
    raf.seek(10)
    println(raf.readLine())
  }
}
