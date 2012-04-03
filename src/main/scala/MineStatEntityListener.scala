package com.dkhenry.minestat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

class MineStatEntityListener(plugin: MineStatPlugin) extends Listener{
	def handlePlayerDeath(p: Player) = {
		plugin.persistance.increment("player",p.getName(),"deaths",1.0) 
		plugin.persistance.increment("server",plugin.serverName,"playersKilled",1.0)	  
	}
  
	@EventHandler 
	def onEntityDeath(event: EntityDeathEvent) = {
		val subject = event.getEntity()
		subject match { 
		  case p: Player => handlePlayerDeath(p) 
		  case _ => 
		}		
	}
}