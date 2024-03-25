package com.sirlarion.battlemusic;

import com.sirlarion.battlemusic.BattleMusic
import com.sirlarion.battlemusic.Music
import net.minecraft.util.Identifier
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.MusicSound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.CommandManager
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents

object Music {
  private val MUSIC_HORSE_ID = Identifier("${BattleMusic.MOD_ID}:horse")
  private val MUSIC_PIZZA_ID = Identifier("${BattleMusic.MOD_ID}:pizzahut")
  // --More 

  public val HORSE = SoundEvent.of(MUSIC_HORSE_ID)
  public val PIZZA = SoundEvent.of(MUSIC_PIZZA_ID) 
  // --More 

  public fun register() {
    Registry.register(Registries.SOUND_EVENT, MUSIC_HORSE_ID, HORSE)
    Registry.register(Registries.SOUND_EVENT, MUSIC_PIZZA_ID, PIZZA)
  }
}

object MusicManager {
  public fun init() {
    Music.register()

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

      val stopNode = CommandManager.literal("stop")
        .executes { _ -> 
          MusicManager.stop()
          1
        }.build()

      dispatcher.getRoot().addChild(battleNode)
      battleNode.addChild(stopNode)
      battleNode.addChild(horseNode)
      battleNode.addChild(pizzaNode)

    })
  }

  public fun play(music: SoundEvent) {
    val player = MinecraftClient.getInstance().musicTracker
    val soundEntry = Registries.SOUND_EVENT.getEntry(music)
    val sound = MusicSound(soundEntry, 0, 0, true)
    player.play(sound)
  }

  public fun stop() {
    val player = MinecraftClient.getInstance().musicTracker
    player.stop()
  }
}
