package com.today36524.logrf.util

import java.text.SimpleDateFormat

import scala.collection.immutable.Seq
import scala.io.Source

object LogReadUtil {
  val dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS")

  /**
    * 将线程池编号名称进行统计返回一个分组后的mapList
    * @return
    */
    def descThreadMapList(filePath:String): Seq[(String, List[(String, String, String)])] = {
      //基于上面的已注释代码，以下方法从12月15日想到
      val reg =
        """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s
          |([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s[\S\s]*(DEBUG|INFO|WARN|ERROR)""".r
      val regLeft =
        """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s
          |([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s""".r
      val regRight = """(DEBUG|INFO|WARN|ERROR)[\S\s]*""".r
      val file = Source.fromFile(filePath)

      /*
      使用正则表达式，将不符合统计标准（非以日期时间开头）的日志行过滤，生成结果数组
       */
      val lineList = for(line <- file.getLines.toList if reg.findFirstIn(line).isInstanceOf[Some[String]])
        yield (regLeft.findFirstIn(line).get //日期时间部分
          ,regRight.replaceAllIn(regLeft.replaceFirstIn(line,""""""),"""""")  //线程名部分
          ,regRight.findFirstIn(line).get  //日志级别以及之后内容部分
        )
      lineList.groupBy(_._2).toList
        .sortWith{case(key1,key2) => key1._2.lengthCompare(key2._2.size)>0}
    }

  /**
    * 将线程池编号名称进行统计，返回一个分组后的只包含服务调用的mapList
    * @return
    */
  def descThreadMapListOfRe(filePath:String): Seq[(String, List[(String, String, String)])] = {
    //基于上面的已注释代码，以下方法从12月15日想到
    val reg =
      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s[\S\s]*(DEBUG|INFO|WARN|ERROR)""".r
//    val regDate =
//      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}""".r
    val regLeft =
      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s""".r
    val regRight = """(DEBUG|INFO|WARN|ERROR)[\S\s]*""".r
    val regRe = """(DEBUG|INFO|WARN|ERROR)[\S\s]*re(quest|sponse)[\S\s]*""".r
    val file = Source.fromFile(filePath)

    /*
    使用正则表达式，将不符合统计标准（非以日期时间开头）的日志行过滤，生成结果数组
     */
    val lineList = for(line <- file.getLines.toList
                       if (reg.findFirstIn(line).isInstanceOf[Some[String]]
      && regRe.findFirstIn(line).isInstanceOf[Some[String]])
    )
      yield (
//        dateFormat.parse(regDate.findFirstIn(line).get).getTime,
        regLeft.findFirstIn(line).get //日期时间部分
        ,regRight.replaceAllIn(regLeft.replaceFirstIn(line,""""""),"""""")  //线程名部分
        ,regRight.findFirstIn(line).get  //日志级别以及之后内容部分
      )
    lineList.groupBy(_._2).toList
      .sortWith{case(key1,key2) => key1._2.lengthCompare(key2._2.size)>0}
  }
}
