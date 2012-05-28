package com.dkhenry.minestat

import org.bukkit.scheduler.BukkitScheduler;

class ServerTickPoller(plugin: MineStatPlugin) extends Runnable {	
	var interval: Long = 40;
	var lastPoll: Long = System.currentTimeMillis() ; 	
			
	def registerWithScheduler(scheduler: BukkitScheduler) { 
		scheduler.scheduleAsyncRepeatingTask(plugin, this, 0, interval) ; 
	}
	
	@Override
	def run() = {
		// Cache the current time 
		val current = System.currentTimeMillis() ;

		// Calculate the Delta 
		var delta = current - lastPoll ;
		
		// Make sure we check for a DivByZero error 
		if(delta <= 0 ) { 
			delta = 1 ; 
		}
		
		val tickRate = this.interval*1000 / delta ;
		
		plugin.persistance.set("server",plugin.serverName,"tickRate",tickRate.doubleValue());
		plugin.persistance.increment("server",plugin.serverName,"ticks",tickRate.doubleValue());
					
		val active = plugin.getServer().getScheduler().getActiveWorkers().size() ;
		plugin.persistance.set("server",plugin.serverName,"activeTasks",active);
		val pending = plugin.getServer().getScheduler().getPendingTasks().size() ;
		plugin.persistance.set("server",plugin.serverName,"pendingTasks",pending);
		
		lastPoll = current ;  
	}	
}