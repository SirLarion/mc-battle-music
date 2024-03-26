package com.sirlarion.battlemusic.util;

import org.slf4j.LoggerFactory;

import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

import com.sirlarion.battlemusic.MusicManager
import com.sirlarion.battlemusic.Music
import com.sirlarion.battlemusic.BattleMusic
import com.sirlarion.battlemusic.MonsterTracker

val MOD_ID = "battle-music"

val LOGGER = LoggerFactory.getLogger(MOD_ID)

val HEALTH_BATTLE_THRESHOLD = 0.7
val MONSTER_COUNT_THRESHOLD = 5
val ANGRY_COUNT_THRESHOLD = 3

object CommandConfig {
  fun init() {
    CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
      val battleNode = CommandManager
        .literal("battle")
        .build()

      val horseNode = CommandManager.literal("horse")
        .executes { _ ->
          MusicManager.play(Music.HORSE)
          1
        }.build()

      val pizzaNode = CommandManager.literal("pizzahut")
        .executes { _ ->
          MusicManager.play(Music.PIZZA)
          1
        }.build()

      val startNode = CommandManager.literal("start")
        .executes { _ -> 
          MusicManager.play()
          1
        }.build()

      val stopNode = CommandManager.literal("stop")
        .executes { _ -> 
          MusicManager.stop()
          1
        }.build()

      val infoNode = CommandManager.literal("info")
        .executes { context -> 
          val player = context.source.player ?: return@executes 0 

          val battleStatus = if (BattleMusic.isBattle) "YES" else "NO"
          val healthPercent = (player.health / player.maxHealth) * 100 
          val nearbyMonsters = MonsterTracker.getNearbyMonsters(player).size
          val angryMonsters = MonsterTracker.getAngryMonsters(player).size

          player.sendMessage(Text.of("Battle in progress: ${battleStatus}"))
          player.sendMessage(Text.of("Health: ${healthPercent}%, Threshold: ${HEALTH_BATTLE_THRESHOLD * 100}%"))
          player.sendMessage(Text.of("Monsters nearby: ${nearbyMonsters}, Threshold: ${MONSTER_COUNT_THRESHOLD}"))
          player.sendMessage(Text.of("Monsters angry: ${angryMonsters}, Threshold: ${ANGRY_COUNT_THRESHOLD}"))

          1
        }.build()

      dispatcher.getRoot().addChild(battleNode)

      battleNode.addChild(startNode)
      battleNode.addChild(stopNode)
      battleNode.addChild(infoNode)

      startNode.addChild(horseNode)
      startNode.addChild(pizzaNode)

    })
  }
}
