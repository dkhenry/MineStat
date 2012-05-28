package com.dkhenry.minestat
import org.bukkit.scheduler.BukkitScheduler

class MongoLog(plugin: MineStatPlugin) extends Runnable {
  
	var interval = 300
	var tid = 0 
	
  	def registerWithScheduler(scheduler: BukkitScheduler) { 
		tid = scheduler.scheduleAsyncRepeatingTask(plugin, this, 0, interval) ;		
	}
	
  	
	@Override
	def run() = {
	  // we need to take a snapshot of persistance and write it to the MongoDB Log 
	  
	}
}