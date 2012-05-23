package com.dkhenry.minestat;

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDamageEvent._ ;
import org.bukkit.entity.LivingEntity

class MineStatEntityListener(plugin: MineStatPlugin) extends Listener{
	def handlePlayerDeath(p: Player) = {	
		plugin.logInfo("player Death By Npe/Enviroment")
		plugin.persistance.increment("player",p.getName(),"deaths",1.0) 
		plugin.persistance.increment("server",plugin.serverName,"playerDeaths",1.0)
		val cause = p.getLastDamageCause();
		cause match { 
		  case ebe: EntityDamageByEntityEvent => {
		    ebe.getDamager() match { 
		      case player: Player => {
		        plugin.persistance.increment("player",p.getName(),"deathByPlayer",1.0)		        		        		        		        
		        plugin.persistance.increment("player",player.getName(),"playerKills",1.0);
		      }
		      case e => {		        
		        plugin.persistance.increment("player",p.getName(),"deathByNpe",1.0)
		      }
		        
		    }
		    
		  }
		  case ebb: EntityDamageByBlockEvent => {
			  plugin.persistance.increment("player",p.getName(),"deathByEnviroment",1.0)
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
		    		   plugin.persistance.increment("player",p.getName(),"deathByEnviroment",1.0)					
				case x =>  println {"Death by unknown cause "+ x.toString }
		    }
		    
		  }
		}

	}
	
	def handleNpeDeath(subject :LivingEntity) {	  
		def subjectName = subject.getClass().getName().split('.').last
		plugin.logInfo("Entity Death By Npe/Enviroment")
		plugin.persistance.increment("npe",subjectName,"deaths",1.0)
		plugin.persistance.increment("server",plugin.serverName,subjectName+"Deaths",1.0);
		val cause = subject.getLastDamageCause();
		cause match { 
		  case ebe: EntityDamageByEntityEvent => {
		    ebe.getDamager() match { 
		      case p: Player => {
		        plugin.persistance.increment("npe",subjectName,"deathByPlayer",1.0)
		        
		        def player =p.getName()		        
		        plugin.persistance.increment("player",player,"npeKills",1.0);
		        plugin.persistance.increment("player",player,subjectName+"Kills",1.0);
		      }
		      case e => {		        
		        plugin.persistance.increment("npe",subjectName,"deathByNpe",1.0)
		      }
		        
		    }
		    
		  }
		  case ebb: EntityDamageByBlockEvent => {
			  plugin.persistance.increment("npe",subjectName,"deathByEnviroment",1.0)
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
		    		   plugin.persistance.increment("npe",subjectName,"deathByEnviroment",1.0)					
				case x =>  println {"Death by unknown cause "+ x.toString }
		    }
		    
		  }
		}

	}		
  
	@EventHandler 
	def onEntityDeath(event: EntityDeathEvent) = {
		plugin.logInfo("Entity Death")
		val subject = event.getEntity()
		plugin.logInfo(subject.getClass().getName().split('.').last + " Death")
		subject match { 
		  case p: Player => handlePlayerDeath(p) 
		  case e: LivingEntity => plugin.logInfo("About to Handle NPE Death") ; handleNpeDeath(e)
		  case _ => plugin.logInfo("Well I could have handled that better")
		}
		plugin.logInfo("Done Handling Event")
	}
}