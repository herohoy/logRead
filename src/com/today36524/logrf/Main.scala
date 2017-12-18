package com.today36524.logrf

import com.today36524.logrf.util.LogReadUtil

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
    /*val raf:RandomAccessFile =
      new RandomAccessFile("E:/detail-productdb-service.2017-11-29.log","r")
    val l:StringBuilder = new StringBuilder
    val s:StringBuilder = new StringBuilder
    println(raf.length())
    raf.seek(raf.length-1)
    println({s.clear();l.append(s.append(raf.readLine()));s.toString})
    println({s.clear();l.append(s.append(raf.readLine()));s.toString})
    println(l.length)
    println(l.size)
    println(s.length)
    println(s.size)*/

//    val regs = """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s"""
//    println("11-29 00:10:00 059 ".matches(regs))


    //参考高手重新依据scala.Source写的代码
//    val reg = """(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\s([0-1][0-9]|2[0-3])(:([0-5][0-9])){2}\s[0-9]{3}\s[\S\s]*(DEBUG|INFO|WARN|ERROR)""".r
//    val file = Source.fromFile("E:/detail-productdb-service.2017-11-29.log")
//    /*
//    使用正则表达式，将不符合统计标准（非以日期时间开头）的日志行过滤，生成结果数组
//     */
//    val lineList = for(line <- file.getLines.toList if reg.findFirstIn(line).isInstanceOf[Some[String]])
//      yield reg.findFirstIn(line).get.split(" ")
//
//    /*
//    将结果数组按照线程池名称编号分组，并根据线程使用的次数降序排列
//     */
//    val lineMapList = lineList.groupBy(_(3)).toList
//      .sortWith{case(key1,key2) => key1._2.lengthCompare(key2._2.size)>0}
//
//    for(m<-lineMapList) println(m._1+":"+m._2.size)

//    val df = new java.text.SimpleDateFormat("HH:mm:ss SSS ")
//    println(df.parse("00:00:00 027 ").getTime)

//    """11-29 00:00:00 239 trans-pool-1-thread-37 DEBUG - com.isuwang.soa.gcprice.service.GcPricesService 1.0.0 queryGcPrices 22704 request header:{"serviceName":"com.isuwang.soa.gcprice.service.GcPricesService","methodName":"queryGcPrices","versionName":"1.0.0","transactionId":"null","transactionSequence":"null","callerFrom":"web","callerIp":"192.168.32.3","operatorId":null,"operatorName":"null","customerId":null,"customerName":"null","""
//      """[\S\s]*DEBUG\s-\s[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+\s[\d]+\srequest\sheader:"""

//    val reg  = """\srequest\sheader:""".r
//    val line = """ request header:{"""
//    println(reg.findFirstIn(line))
//      val regRes =
//      """[\S\s]*DEBUG\s-\s[\S.]+[A-Z]+[\S]*\s1\.0\.0\s[\S]+\sOptional\.empty\sresponse\sheader:""".r
//      val line = """11-29 00:00:00 232 trans-pool-1-thread-31 DEBUG - com.isuwang.soa.gcprice.service.GcPricesService 1.0.0 queryGcPrices Optional.empty response header:Optio"""
//      println(regRes.findFirstIn(line))

    //基于上面的已注释代码，以下方法从12月15日想到

    val fileName = "D:/detail-productdb-service.2017-11-29.log"
//    val lineMapList = LogReadUtil.descListOfServiceMethods(fileName)
//
//    println("服务名:总调用次数")
//    for(m<-lineMapList)
//      println(m._1+":"+m._2.size)
//
//    println(lineMapList.size)

//    val lineMapList = LogReadUtil.avgTimelenOfServiceMethods(fileName)
//
//    println("线程名:统计数字")

    val avgList = LogReadUtil.analyzeAverageTime(fileName)

    println(avgList.size)

  }
}
