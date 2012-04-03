package com.dkhenry.minestat

import java.util.logging.Logger
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.block.LeavesDecayEvent
import scala.collection.mutable.HashMap
import org.bukkit.plugin.PluginManager

/**
 * For Persistance 
 * 	typ Key defines a type
 * 	name Key defines a named object of type typ 
 *  indicator Defined the indicator for this object
 * 	Value is the Value to persist ( either increment decrement or store )
 * 
 *  The Only two methods this needs are store and retrieve
 */
trait Persist {  
  def store(typ: String, name: String,indicator: String, value: Double)
  def retrieve(typ: String, name: String, indicator: String): Option[Double]  
  def filter(typ:String) : Iterable[((String,String,String),Double)]
  def filter(typ: String, name: String) : Iterable[((String,String,String),Double)]
  
  // We Want 
  //	Object: <name> 
  //		type: <type>
  //		indicators: 
  //			<name> : <value> 
  //
  def print() 
  
  def lookupAndAct(key: (String,String,String), value:Double)(action: Double => Double) = { 
    retrieve(key._1,key._2,key._3) match { 
      case Some(v) => store(key._1,key._2,key._3,action(v)) 
      case None => store(key._1,key._2,key._3,value)
    }    
  }  
  def increment(typ: String, name: String, indicator: String, value: Double) { 
    lookupAndAct((typ,name,indicator),value) {v => v + value}
  } 
  def decrement(typ: String, name: String, indicator: String, value: Double) {
    lookupAndAct((typ,name,indicator),value) {v => v - value}
  }
  def set(typ: String, name: String, indicator: String, value: Double) { 
    lookupAndAct((typ,name,indicator),value) {v => value}
  }
  
}

class MemoryPersist extends Persist {
  var data: HashMap[(String,String,String),Double] = new HashMap[(String,String,String),Double]
  
  def store(typ: String, name: String, indicator: String, value: Double) = { 
    data += ((typ,name,indicator)-> value)
  }
  
  def retrieve(typ: String, name: String, indicator:String): Option[Double] = {
    data get ((typ,name,indicator))
  }
  
  def filter(typ:String) : Iterable[((String,String,String),Double)] = {
    data filter { k => k._1._1 == typ }
  }
  def filter(typ: String, name: String) : Iterable[((String,String,String),Double)] = {
    data filter { k => k._1._1 == typ && k._1._2 == name }   
  }
  
  def print() {
    // get All the Objects and their types    
    val objects = (Set[(String,String)]() /: data ){ (list , e ) => list + ((e._1._1,e._1._2)) }
    objects map { set => 
      println {"Object: "+set._2 +"\n\ttype: "+ set._1+"\n\tindicators:"}
      val indicators = data filter { k => k._1._1 == set._1 && k._1._2 == set._2 }
      indicators map { i =>
        println { "\t\t"+ i._1._3 + " = " + i._2.toString()}
      }      
    }
  }
}



class MineStatPlugin extends JavaPlugin {
  val logger = Logger.getLogger("Minecraft.Minestat")
  
  val persistance = new MemoryPersist()
  def serverName = "Craftbukkit"
    
  // The Listeners
  val blockListener = new MineStatBlockListener(this)
  
  override def onEnable = {
    logInfo("MineStat Enabled!") 
    /* Register the Listeners */
	val pm: PluginManager = this.getServer().getPluginManager() ;
	
    // The Block Events		
	pm.registerEvents(blockListener, this) ; 
  }
  
  override def onDisable = {
    logInfo("MineStat Disabled.")
    persistance.print() 
  }
  
  def logInfo(msg:String) = logger.info(msg)
  def logWarning(msg:String) = logger.warning(msg)
}
