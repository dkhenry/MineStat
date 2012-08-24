package com.dkhenry.minestat
import org.bukkit.scheduler.BukkitScheduler


/**
 * Hierarachy
 * collection: minelog
 * each document should have this form 
 * {
 *     type: server
 *     name: Craftbukkit
 *     date: new DateTime()
 *     indicators [ 
 *     	{
 *     		name: ticks,
 *     		value: 3434343
 *     	},
 *     	{
 *     		name: ticksPerSecond,
 *     		value: 20
 *     	},
 *     	{
 *     		name: playerDeaths,
 *     		value: 50,
 *     		tags: { 
 *     			deathsByNpe: 30
 *     			deathsByEnviroment: 10 
 *     			deathsByPlayers 10
 *     		}
 *     	},
 */
class MongoLog(plugin: MineStatPlugin) extends Runnable {
  
	var interval = 300
	var tid = 0 
	
  	def registerWithScheduler(scheduler: BukkitScheduler) { 
		tid = scheduler.scheduleAsyncRepeatingTask(plugin, this, 0, interval) 
	}
	
  	
	@Override
	def run() = {
		plugin.logWarning("Running Persistance")
	  //plugin.persistance.map { 
	    	    
	  //}
	  // we need to take a snapshot of persistance and write it to the MongoDB Log 
	  
	}
}