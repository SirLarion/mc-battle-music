package com.sirlarion.battlemusic

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.util.Identifier
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.LivingEntity

import com.sirlarion.battlemusic.util.*

object MonsterTracker {
  private val BATTLE_RANGE = 20.0

  private val monstersByPlayer: MutableMap<Int, MutableList<LivingEntity>> = mutableMapOf()

  fun init() {
    EntityTrackingEvents.START_TRACKING.register { entity, player -> 
      if(entity is LivingEntity && entity is Monster) {
        LOGGER.debug("Tracking ${entity.name} for ${player.name}")
        val arr = monstersByPlayer.get(player.id)
        if(arr != null) {
          arr.add(entity)
        } else {
          monstersByPlayer.put(player.id, mutableListOf(entity))
        }
      }
    }

    EntityTrackingEvents.STOP_TRACKING.register { entity, player -> 
      if(entity is LivingEntity && entity is Monster) {
        LOGGER.debug("Stopping tracking ${entity.name} for ${player.name}")
        val arr = monstersByPlayer.get(player.id)
        if(arr != null) {
          arr.remove(entity)
        }
        if(!BattleMusic.hasRequirementsForBattle(player)) {
          BattleMusic.queueBattleEnd()
        }
      }
    }
  }

  fun getAngryMonsters(player: PlayerEntity): List<LivingEntity> {
    val monsters = monstersByPlayer.get(player.id) ?: listOf()
    return monsters.filter { monster -> monster is HostileEntity && monster.getTarget() == player }
  }

  fun getNearbyMonsters(player: PlayerEntity): List<LivingEntity> {
    val monsters = monstersByPlayer.get(player.id) ?: listOf()
    return monsters.filter { monster -> monster.isInRange(player, BATTLE_RANGE) }
  }

  fun hasEnoughMonsters(player: PlayerEntity): Boolean {
    val nearby = getNearbyMonsters(player)
    val angry = getAngryMonsters(player)

    val enoughNearby = nearby.size >= MONSTER_COUNT_THRESHOLD
    val enoughAngry = angry.size >= ANGRY_COUNT_THRESHOLD

    return enoughNearby && enoughAngry
  }

}
