package com.dkhenry.minestat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent._ ;

class MineStatEntityListener(plugin: MineStatPlugin) extends Listener{
	def handlePlayerDeath(p: Player) = {		
		plugin.persistance.increment("player",p.getName(),"deaths",1.0) 
		plugin.persistance.increment("server",plugin.serverName,"playersKilled",1.0)	  
	}
	
	def handleNpeDeath(subject :Entity) { 
		val cause = subject.getLastDamageCause();
		plugin.persistance.increment("npe",subject.getClass().toString(),"deaths",1.0)
		plugin.persistance.increment("server",plugin.serverName,subject.getClass().toString()+"Deaths",1.0);
		cause match { 
		  case ebe: EntityDamageByEntityEvent => {
		    ebe.getDamager() match { 
		      case p: Player => {
		        plugin.persistance.increment("npe",subject.getClass().toString(),"deathByPlayer",1.0)
		      }
		      case e => {		        
		        plugin.persistance.increment("npe",subject.getClass().toString(),"deathByNpe",1.0)
		      }
		        
		    }
		    
		  }
		  case ebb: EntityDamageByBlockEvent => {
			  plugin.persistance.increment("npe",subject.getClass().toString(),"deathByEnviroment",1.0)
		  }
		  case _ => {
		    cause.getCause() match { 
		    	case DamageCause.CONTACT |
		    		 DamageCause.DROWNING | 
		    		 DamageCause.FALL | 
		    		 DamageCause.FIRE |
		    		 DamageCause.FIRE_TICK |
		    		 DamageCause.LAVA |
		    		 DamageCause.LIGHTNING =>
		    		   plugin.persistance.increment("npe",subject.getClass().toString(),"deathByEnviroment",1.0)					
				case x =>  println {"Death by unknown cause "+ x.toString }
		    }
		    
		  }
		}
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