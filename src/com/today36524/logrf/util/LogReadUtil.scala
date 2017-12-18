package com.today36524.logrf.util

import java.text.SimpleDateFormat

import scala.collection.immutable.Seq
import io.Source
import scala.util.matching.Regex
import scala.annotation.tailrec

object LogReadUtil {
  val dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS ")
  val sdf = new SimpleDateFormat("MM-dd HH:mm:ss SSS")

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
      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s[\S\s]*DEBUG""".r
//    val regDate =
//      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}""".r
    val regLeft =
      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s""".r
    val regRight = """(DEBUG|INFO|WARN|ERROR)[\S\s]*""".r
    val regRe = """DEBUG[\S\s]*(request header:\{"serviceName"|response header:Optional)[\S\s]*""".r
//    val regReq = """DEBUG[\S\s]*request header:\{"serviceName"[\S\s]*""".r
//    val regRes = """DEBUG[\S\s]*response header:Optional[\S\s]*""".r
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
    /*
    按照线程名分组，转化为list，根据线程使用次数排序
     */
    lineList.groupBy(_._2).toList
      .sortWith{case(key1,key2) => key1._2.lengthCompare(key2._2.size)>0}
  }

  /**
    * 统计每个服务调用次数
    * @param filePath 文件路径字符串
    * @return
    */
  def descListOfServiceMethods(filePath:String): List[(String, List[(String, String)])] = {
    //过滤出服务名（类名、版本号和方法名）
    val reg: Regex =
      """[\S\s]*DEBUG\s-\s[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+\s[\d]+\srequest\sheader:""".r
    //单单匹配类名、版本和方法名，用于分组
    val regSn: Regex =
      """[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+""".r
    val file = Source.fromFile(filePath)
    val lineList = for(line <- file.getLines.toList
                       if reg.findFirstIn(line).isInstanceOf[Some[String]]
    )
      yield (
        regSn.findFirstIn(line).get  //服务名
        ,line
      )
    lineList.groupBy(_._1).toList
      .sortWith{case(key1,key2) => key1._2.lengthCompare(key2._2.size)>0}
  }


  /**
    * 计算平均时长的失败尝试，根据实测，使用以下方式耗时长、且从具体步骤来说偏向于一步一步的思维，集成度不高
    * @param filePath 文件路径
    * @return
    */
  def avgTimelenOfServiceMethods(filePath:String) ={
    val regDate =
      """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s""".r
    val regRight = """(DEBUG|INFO|WARN|ERROR)\s-\s[\S\s]*""".r
    //过滤出服务名（类名、版本号和方法名）
    val reg: Regex =
      """[\S\s]*DEBUG\s-\s[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+\s[\d]+\srequest\sheader:""".r
    //过滤出服务名response（类名、版本号和方法名）
    val regRes: Regex =
      """[\S\s]*DEBUG\s-\s[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+\sOptional\.empty\sresponse\sheader:""".r
    //单单匹配类名、版本和方法名，用于分组
    val regSn: Regex =
      """[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+""".r
    val file = Source.fromFile(filePath)
    val lines = file.getLines/* iterator只允许迭代一次，因此还是需要转化为集合 */.toList
    val lineIter = for(line <- lines
                       if (
                       reg.findFirstIn(line).isInstanceOf[Some[String]]
                        && regDate.findFirstIn(line).isInstanceOf[Some[String]]
                         )
    )
      yield (
        regDate.findFirstIn(line).get //日期时间部分
        ,regRight.replaceAllIn(regDate.replaceFirstIn(line,""""""),"""""")  //线程名部分
        ,regSn.findFirstIn(line).get  //服务名部分
        ,line  //内容部分
      )

    val requestIter = for (line <- lineIter) yield (
      dateFormat.parse(line._1).getTime
        ,line._1 //日期时间部分
    ,line._2  //线程名部分
    ,line._3  //服务名部分
    ,"request"  //内容部分
      )


    val respIter = for(line <- lines
                       if (
                         regRes.findFirstIn(line).isInstanceOf[Some[String]]
                           && regDate.findFirstIn(line).isInstanceOf[Some[String]]
                         )
    )
      yield (
        regDate.findFirstIn(line).get //日期时间部分
        ,regRight.replaceAllIn(regDate.replaceFirstIn(line,""""""),"""""")  //线程名部分
        ,regSn.findFirstIn(line).get  //服务名部分
        ,line  //内容部分
      )
    val responseIter = for (line <- respIter) yield (
      dateFormat.parse(line._1).getTime
      ,line._1 //日期时间部分
      ,line._2  //线程名部分
      ,line._3  //服务名部分
      ,"response"  //内容部分
    )

    //使用zip拉链绑定两个list，可以有这样一个思路，但没有连续有用的执行方式，留待以后优化
//    requestIter.zip(responseIter)

    val millsResList = responseIter.indices.map(i => (
      responseIter(i)._4, responseIter(i)._1 - requestIter(i)._1
    )).groupBy(_._1)


    val resList = for((k,v) <- millsResList) yield (k -> v.reduce((a,b) => (a._1,a._2+b._2)))

    resList
  }


  /**
    * 以下代码是按照雷华哲的完成方式摘抄的部分
    */
  type TIME = String
  type THREAD = String
  type SERVICE = String
  type WAYS = String
  type METHOD = String
  val regex =  """(\d{2}-\d{2} \d{2}:\d{2}:\d{2} \d{3}) ([^\s]+) DEBUG ([^\s]+) ([^\s]+) ([^\s]+) ([^\s]+) (.+)""".r
  /**
    * 学习雷华哲的完成方式写出的函数
    * @param filePath 文件路径
    * @return
    */
  def analyzeAverageTime(filePath:String) = {
    val source = Source.fromFile(filePath, "UTF-8")

    val lists:List[(TIME,THREAD,SERVICE,METHOD,WAYS)] = source.getLines().collect{
      case regex(timestamp, thread,c,service,version,method,requests) => (timestamp,thread,service,method,requests)
    }.toList

    val requests:List[(TIME,THREAD,SERVICE,WAYS)] = lists.filter(_._5.contains("request")).map (line =>{
      (line._1,line._2,line._3+"."+line._4,"request")
    })

    val response:List[(TIME,THREAD,SERVICE,WAYS)] = lists.filter(_._5.contains("response")).map (line =>{
      (line._1,line._2,line._3+"."+line._4,"response")
    })
    //    得到所有 request  和 response 结果
    val totalServices = (requests ++ response).sortWith((t1,t2) => t1._1 < t2._1)//.take(10)

//    totalServices
    processByTailRec(totalServices,Map[(THREAD,SERVICE),TIME]())
    totalServices
  }
  /**
    * 使用尾递归的方法，消除 var 和 mutable Map
    * @param array
    * @param pendings
    */
  @tailrec
  def processByTailRec(array:List[(TIME,THREAD,SERVICE,WAYS)],pendings:Map[(THREAD,SERVICE),TIME]): Unit ={

    array match {
      case (time,thread,service,"response") :: tail =>
        pendings.get((thread, service)) match {

          case Some(time0) =>
            val msg =
              s"Thread: ${thread} Service: ${service}  average-time:   ${BigDecimal(sdf.parse(time).getTime - sdf.parse(time0).getTime)} ms"
            println(msg)

            processByTailRec(tail, pendings - ((thread, service)))

          case None =>
            processByTailRec(tail, pendings)
        }

      case (time, thread, service, "request") :: tail =>
        processByTailRec(tail, pendings + ((thread, service) -> time) )

      case Nil => None
    }
  }
}
