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

class MineStatBlockListener(plugin: MineStatPlugin) extends Listener {	
	@EventHandler
	def onBlockPlace(event: BlockPlaceEvent) = {
	  val mat = event.getBlock().getType()
	  plugin.persistance.increment("block",mat.name(),"BlocksPlaced",1.0)
	  plugin.persistance.increment("server",plugin.serverName,"BlocksPlaced",1.0)
	  
  	  //TODO: Increment the Player Information
	  def player = event.getPlayer().getName()
	  plugin.persistance.increment("player",player,"BlocksPlaced",1.0);
	}
	@EventHandler
	def onBlockBreak(event: BlockBreakEvent) = {	  
	  val mat = event.getBlock().getType()
	  plugin.persistance.increment("block",mat.name(),"BlocksDestroyed",1.0)
	  plugin.persistance.increment("server",plugin.serverName,"BlocksDestroyed",1.0)
	  
	  //TODO: Increment the Player Information
	  def player = event.getPlayer().getName()
	  plugin.persistance.increment("player",player,"BlocksDestroyed",1.0);
	}
	@EventHandler 
	def onBlockSpread(event: BlockSpreadEvent) = {
	  val mat = event.getBlock().getType()
	  plugin.persistance.increment("block",mat.name(),"BlocksSpread",1.0)
	  plugin.persistance.increment("server",plugin.serverName,"BlocksSpread",1.0)
	}
	@EventHandler 
	def onLeavesDecay(event: LeavesDecayEvent) = {
	  val mat = event.getBlock().getType()
	  plugin.persistance.increment("block",mat.name(),"BlocksDecayed",1.0)
	  plugin.persistance.increment("server",plugin.serverName,"BlocksDecayed",1.0)	  
	}
}