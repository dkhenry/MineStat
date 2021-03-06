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

// MongoDB Persistance 
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
import org.joda.time._


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
  def all(): Iterable[((String,String,String),Double)]  
  
  // We Want 
  //	Object: <name> 
  //		type: <type>
  //		indicators: 
  //			<name> : <value> 
  //
  def print() = {
    all map { case ((typ,name,indicator),value) => 
      println {"Object: "+ name +"\n\ttype: "+ typ+"\n\tindicators:"}
      val indicators = all filter { k => k._1._1 == typ && k._1._2 == name }
      indicators map { i =>
        println { "\t\t"+ i._1._3 + " = " + i._2.toString()}
      }      
    }
  }
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
  def map[B]()( f: ((String,String,String),Double) => B) { 
    all map { case(a,b) => f(a,b) }    
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

  def all = { 
  	  data
  }
 
}

class MongoPersist extends Persist {
  RegisterJodaTimeConversionHelpers()

  // Connect to the database
  lazy val _mongoConn = MongoConnection()  
  val data = _mongoConn("minestat")
  
  def store(typ: String, name: String, indicator: String, value: Double) = {
	  // true -> Upsert , false -> multiple ( only update one document if you match the filter ) 
	  data(typ).update(DBObject("name"->name,"indicator"->indicator),$set("value"-> value),true,false)
  }
  
  def retrieve(typ: String, name: String, indicator:String): Option[Double] = {
	  data(typ).findOne(DBObject("name"->name,"indicator"->indicator)) match { 
	    case Some(o) => o.getAs[Double]("value")
	    case _ => None
	  }	  
  }
  
  def filter(typ:String) : Iterable[((String,String,String),Double)] = {
	  (Map[(String,String,String),Double]() /: data(typ).find())((ret,obj) => {	    
	    ret ++ Map(( ( typ,obj.getAsOrElse[String]("name",""),obj.getAsOrElse[String]("indicator","") ),obj.getAsOrElse[Double]("value",0.0)))
	  }) 	  
  }
  
  def filter(typ: String, name: String) : Iterable[((String,String,String),Double)] = {
    (Map[(String,String,String),Double]() /: data(typ).find(DBObject("name"->name)))((ret,obj) => {	    
	    ret ++ Map(( ( typ,obj.getAsOrElse[String]("name",""),obj.getAsOrElse[String]("indicator","") ),obj.getAsOrElse[Double]("value",0.0)))
	  })	  
  }
  
  // Iterate over the collectionNames.
  // Add them to a giant Map 
  def all() = { 
    (Map[(String,String,String),Double]() /: data.collectionNames)((r,name) => {           
    	r ++ (Map[(String,String,String),Double]() /: data(name).find())((ret,obj) => { 
    		ret ++ Map(( ( "foo",obj.getAsOrElse[String](name,""),obj.getAsOrElse[String]("indicator","") ),0.0 ))
    	})
    })
  }
  
  def clear() = { 
    data.collectionNames foreach { name => data(name).remove(DBObject()) }
  }
  
  def load(d: Iterable[((String,String,String),Double)]) = { 
    clear()
    // load up each value    
  }
}



class MineStatPlugin extends JavaPlugin {
  val logger = Logger.getLogger("Minecraft.Minestat")
  
  val persistance = new MongoPersist()
  def serverName = this.getServer().getName()    
  
  // The Listeners
  val blockListener = new MineStatBlockListener(this)
  val entityListener = new MineStatEntityListener(this)
  val playerListener = new MineStatPlayerListener(this)
  val tickPoller = new ServerTickPoller(this)
  
  val scorekeeper = new MongoLog(this)
  
  override def onEnable = {    
    logInfo("Enableing MineStat!")
    /* Register the Listeners */
	val pm: PluginManager = this.getServer().getPluginManager() 
	
    // The Block Events		
	pm.registerEvents(blockListener, this) 
	// The Entity Events
	pm.registerEvents(entityListener, this)
	// The Player Listener
	pm.registerEvents(playerListener, this)
	
	// Reset the server wide statistics 
	persistance.set("server",serverName,"numberOfPlayers",0.0)
	
	// Server Events	
	tickPoller.registerWithScheduler(getServer().getScheduler()) 
	scorekeeper.registerWithScheduler(getServer().getScheduler())

	//TODO: We need to enable JMX here

	
	logInfo("MineStat Enabled!")
  }
  
  override def onDisable = {
    logInfo("MineStat Disabled.")
    persistance.print() 
  }
  
  def logInfo(msg:String) = logger.info(msg)
  def logWarning(msg:String) = logger.warning(msg)
}
