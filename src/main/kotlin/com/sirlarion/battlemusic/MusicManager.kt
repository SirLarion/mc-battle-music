package com.sirlarion.battlemusic;

import net.minecraft.util.Identifier
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.MusicSound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.client.MinecraftClient

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents

import com.sirlarion.battlemusic.util.*

object Music {
  private val MUSIC_HORSE_ID = Identifier("${MOD_ID}:horse")
  private val MUSIC_PIZZA_ID = Identifier("${MOD_ID}:pizzahut")
  // --More 

  val HORSE = SoundEvent.of(MUSIC_HORSE_ID)
  val PIZZA = SoundEvent.of(MUSIC_PIZZA_ID) 
  // --More 

  fun register() {
    Registry.register(Registries.SOUND_EVENT, MUSIC_HORSE_ID, HORSE)
    Registry.register(Registries.SOUND_EVENT, MUSIC_PIZZA_ID, PIZZA)
  }
}

object MusicManager {
  fun init() {
    Music.register()
  }

  private fun getRandom(): SoundEvent = arrayOf(Music.HORSE, Music.PIZZA).random()


  fun play(music: SoundEvent? = null) {
    val player = MinecraftClient.getInstance().musicTracker
    val soundEntry = Registries.SOUND_EVENT.getEntry(music ?: getRandom())
    val sound = MusicSound(soundEntry, 0, 0, true)
    player.play(sound)
  }

  fun stop() {
    val player = MinecraftClient.getInstance().musicTracker
    player.stop()
  }
}
