package com.sirlarion.battlemusic;

import java.util.LinkedList
import kotlin.random.Random
import kotlin.collections.mutableListOf

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.MusicSound
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientEntityManager

import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.api.ModInitializer

import com.sirlarion.battlemusic.MusicManager
import com.sirlarion.battlemusic.util.*
import com.sirlarion.battlemusic.MonsterTracker

object BattleMusic : ModInitializer {
  private var isBattle = false

  private var ticksUntilBattleStart = -1
  private var ticksUntilBattleEnd = -1

  fun getBattleLifecycleMessages(): List<Text> {
    val msgs: MutableList<Text> = mutableListOf()
    if (isBattle) {
      msgs.add(Text.of("Battle in progress: YES"))
      if(ticksUntilBattleStart != -1) {
        msgs.add(Text.of("Ticks until battle music: ${ticksUntilBattleStart}"))
      }
      if(ticksUntilBattleEnd != -1) {
        msgs.add(Text.of("Ticks until battle end: ${ticksUntilBattleEnd}"))
      }
    } else {
      msgs.add(Text.of("Battle in progress: NO"))
    }

    return msgs.toList()
  }
	override fun onInitialize() {
		LOGGER.info("Initializing ${MOD_ID}")

    MusicManager.init()
    MonsterTracker.init()
    CommandConfig.init()

    ClientTickEvents.END_CLIENT_TICK.register { _ -> 
      if (isBattle) {
        if (ticksUntilBattleStart == 0) MusicManager.play()
        if (ticksUntilBattleStart > -1) ticksUntilBattleStart -= 1

        if (ticksUntilBattleEnd == 0) {
          MusicManager.stop()
          isBattle = false
        }
        if (ticksUntilBattleEnd > -1) ticksUntilBattleEnd -= 1
      }
    }

    ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity, _, amount ->  
      if (entity is PlayerEntity) {
        if(!isBattle) {
          if(hasRequirementsForBattle(entity, amount)) {
            queueBattleStart()
          }
        }
        else if (ticksUntilBattleEnd != -1) ticksUntilBattleEnd = -1
      }
      true
    }
	}

  fun hasRequirementsForBattle(player: PlayerEntity, damage: Float = 0.0f): Boolean {
    val healthBelowThreshold = (player.health - damage) / player.maxHealth <= HEALTH_BATTLE_THRESHOLD
    val enoughMonsters = MonsterTracker.hasEnoughMonsters(player)

    return healthBelowThreshold && enoughMonsters
  }

  private fun queueBattleStart() {
    isBattle = true
    ticksUntilBattleStart = Random.nextInt(15, 45)
    ticksUntilBattleEnd = -1
  }

  fun queueBattleEnd() {
    if(ticksUntilBattleEnd == -1) ticksUntilBattleEnd = Random.nextInt(100, 200)
  }
}
