package com.sirlarion.battle_music;

import com.sirlarion.battle_music.BattleMusic
import com.sirlarion.battle_music.Music
import net.minecraft.util.Identifier
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.MusicSound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.client.MinecraftClient

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
  }

  public fun play(music: SoundEvent) {
    val player = MinecraftClient.getInstance().musicTracker
    val soundEntry = Registries.SOUND_EVENT.getEntry(music)
    val sound = MusicSound(soundEntry, 0, 0, true)
    player.play(sound)
  }
}
