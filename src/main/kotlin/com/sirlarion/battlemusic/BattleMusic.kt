package com.sirlarion.battlemusic;

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.MusicSound
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.tree.LiteralCommandNode

import com.sirlarion.battlemusic.MusicManager

object BattleMusic : ModInitializer {
  public val MOD_ID = "battle-music"

  private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		logger.info("Initializing battle-music")

    MusicManager.init()

    ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity, _, _ ->  
      if (entity.isPlayer()) {
        MusicManager.play(Music.HORSE)
      }
      true
    }
	}
}
