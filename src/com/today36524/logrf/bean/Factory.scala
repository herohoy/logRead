package com.today36524.logrf.bean

/**
  * 工厂bean类
  * @param name 工厂名称
  * @param factoryType 工厂类型
  * @param productType 产品类型
  */
case class Factory(name:String,factoryType:Int,productType:Int) {
  def factoryTypeShow(i:Int) : String =
    i match {
      case 1 => "重工厂"
      case 2 => "制造工厂"
      case _ => "未知"
    }
}
