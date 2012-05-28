package com.dkhenry.minestat

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent;
import scala.collection.mutable.HashMap

class MineStatPlayerListener(plugin: MineStatPlugin) extends Listener {
	var playerHash = new HashMap[String,Long]()
	
	@EventHandler
	def onPlayerJoin(event: PlayerJoinEvent) = {
		def name = event.getPlayer().getName()
		plugin.persistance.increment("server",plugin.serverName,"numberOfPlayers",1.0);
		plugin.persistance.increment("player",name,"numberOfLogins",1.0);
		plugin.persistance.set("player",name,"active",1.0);
		playerHash put (name , System.currentTimeMillis())
	}

	@EventHandler
	def onPlayerQuit(event: PlayerQuitEvent) = {
		def name = event.getPlayer().getName()
		plugin.persistance.increment("server",plugin.serverName,"numberOfPlayers",-1.0);
		plugin.persistance.set("player",name,"active",0.0);
		
		playerHash get name map { t => 
			plugin.persistance.increment("player",name,"timeOnServer",System.currentTimeMillis() - t);
		}  		
	}

	@EventHandler
	def onPlayerMove(event: PlayerMoveEvent) = {	
		val player = event.getPlayer();
		val from = event.getFrom()
		val to = event.getTo();
		
		val distance = Math.sqrt(Math.pow(to.getX() - from.getX(), 2) + Math.pow(to.getY() - from.getY(), 2) + Math.pow(to.getZ() - from.getZ(), 2));

		// Increment the per-Player stats
		plugin.persistance.increment("player",player.getName(),"distanceMoved",distance);
		plugin.persistance.increment("server",plugin.serverName,"playerDistanceMoved",distance);		
	}
}