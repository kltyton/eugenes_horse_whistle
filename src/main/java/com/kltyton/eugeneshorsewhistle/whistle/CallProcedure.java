package com.kltyton.eugeneshorsewhistle.whistle;

import com.kltyton.eugeneshorsewhistle.config.ModConfig;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModSounds;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CallProcedure {
	private static final double ENTITY_SEARCH_RADIUS = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getEntitySearchRadius();
	private static final int MAX_TELEPORT_OFFSET = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getMaxTeleportOffset();
	private static final double TELEPORT_DISTANCE_THRESHOLD = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getTeleportDistanceThreshold();

	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof Level _level) {
			float pitch;
			pitch = 0.12f + (new Random().nextFloat());
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), EugenesHorseWhistleModSounds.WHISTLE, SoundSource.PLAYERS, 6, pitch);
				if (!_level.isClientSide() && _level.getServer() != null) {
					_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, entity.position(), entity.getRotationVector(), _level instanceof ServerLevel ? (ServerLevel) _level : null, 4,
							entity.getName().getString(), entity.getDisplayName(), _level.getServer(), entity), "cpm animate @s whistle");
				} else {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), EugenesHorseWhistleModSounds.WHISTLE, SoundSource.PLAYERS, 6, pitch, false);
				}
			}
			final Vec3 playerCenter = new Vec3((entity.getX()), (entity.getY()), (entity.getZ()));

// 遍历当前玩家驯服的所有马类生物
			for (AbstractHorse tamedEntity : _level.getEntitiesOfClass(AbstractHorse.class, new AABB(playerCenter, playerCenter).inflate(ENTITY_SEARCH_RADIUS), e -> e instanceof AbstractHorse && e.isTamed() && Objects.equals(e.getOwnerUUID(), entity.getUUID()))) {
				if (!tamedEntity.isVehicle()) {
					// 检查马是否装备了鞍
					if (tamedEntity.isSaddled()) {
						// 传送装备了鞍的马到当前玩家位置
						tamedEntity.teleportTo(playerCenter.x(), playerCenter.y(), playerCenter.z());
					}
					final Vec3 _center = new Vec3((entity.getX()), (entity.getY()), (entity.getZ()));
					List<Entity> _entfound;
					_entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(ENTITY_SEARCH_RADIUS), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound)
						if (entityiterator instanceof AbstractHorse) {
							AbstractHorse horse;
							horse = (AbstractHorse) entityiterator;
							if (horse.isTamed()) {
								UUID ownerUUID = horse.getOwnerUUID();

								MinecraftServer server = world.getServer();
								ServerPlayer player;
								player = Objects.requireNonNull(server).getPlayerList().getPlayer(entity.getUUID());
								if (player != null) {
									UUID playerUUID = player.getUUID();
									if (Objects.requireNonNull(ownerUUID).equals(playerUUID)) {
										if (!horse.isVehicle()) {
											if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
												_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 1, false, false));
											if (!entityiterator.isVehicle()) {
												Mob _mobEnt = (Mob) entityiterator;
												if (!_mobEnt.isLeashed()) {
													BlockPos ownerPos = player.blockPosition();
													Vec3 ownerVec = Vec3.atCenterOf(ownerPos);
													double distance = horse.position().distanceTo(ownerVec);
													if (distance > TELEPORT_DISTANCE_THRESHOLD) {
														Random random = new Random();
														int maxOffset = MAX_TELEPORT_OFFSET;
														int offsetX = random.nextInt(maxOffset * 2 + 1) - maxOffset;
														int offsetZ = random.nextInt(maxOffset * 2 + 1) - maxOffset;
														float previousHealth = horse.getHealth();
														BlockPos targetPos = new BlockPos(ownerPos.getX() + offsetX, ownerPos.getY(), ownerPos.getZ() + offsetZ);
														while (!world.getBlockState(targetPos).isAir()) {
															offsetX = random.nextInt(maxOffset * 2 + 1) - maxOffset;
															offsetZ = random.nextInt(maxOffset * 2 + 1) - maxOffset;
															targetPos = new BlockPos(ownerPos.getX() + offsetX, ownerPos.getY(), ownerPos.getZ() + offsetZ);
														}
														horse.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
														float healthChange = previousHealth - horse.getHealth();
														if (healthChange > 0) {
															horse.teleportTo(ownerPos.getX(), ownerPos.getY(), ownerPos.getZ());
															horse.getNavigation().stop();
														}
														horse.getNavigation().stop();
													}
													if (entityiterator instanceof Horse) {
														new Object() {
															private int ticks = 0;

															public void startDelay(LevelAccessor world) {
																ServerTickEvents.END_SERVER_TICK.register((server) -> {
																	this.ticks++;
																	if (this.ticks == 30) {
																		if (world instanceof Level _level) {
																			if (!_level.isClientSide()) {
																				_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()),
																						BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.horse.angry")), SoundSource.AMBIENT, 5, 1);
																			} else {
																				_level.playLocalSound((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.horse.angry")),
																						SoundSource.AMBIENT, 5, 1, false);
																			}
																		}
																	}
																});
															}
														}.startDelay(world);
													}
												}
												if (entityiterator instanceof Donkey) {
													new Object() {
														private int ticks = 0;

														public void startDelay(LevelAccessor world) {
															ServerTickEvents.END_SERVER_TICK.register((server) -> {
																this.ticks++;
																if (this.ticks == 30) {
																	if (world instanceof Level _level) {
																		if (!_level.isClientSide()) {
																			_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()),
																					BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.donkey.angry")), SoundSource.AMBIENT, 5, 1);
																		} else {
																			_level.playLocalSound((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.donkey.angry")),
																					SoundSource.AMBIENT, 5, 1, false);
																		}
																	}
																}
															});
														}
													}.startDelay(world);
												}
												if (entityiterator instanceof Mule) {
													new Object() {
														private int ticks = 0;

														public void startDelay(LevelAccessor world) {
															ServerTickEvents.END_SERVER_TICK.register((server) -> {
																this.ticks++;
																if (this.ticks == 30) {
																	if (world instanceof Level _level) {
																		if (!_level.isClientSide()) {
																			_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.mule.angry")),
																					SoundSource.AMBIENT, 5, 1);
																		} else {
																			_level.playLocalSound((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.mule.angry")),
																					SoundSource.AMBIENT, 5, 1, false);
																		}
																	}
																}
															});
														}
													}.startDelay(world);
												}
												Mob _entity = (Mob) entityiterator;
												_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
												new Object() {
													private int ticks = 0;

													public void startDelay(LevelAccessor world) {
														ServerTickEvents.END_SERVER_TICK.register((server) -> {
															this.ticks++;
															if (this.ticks == 40) {
																Mob _entity = (Mob) entityiterator;
																_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																new Object() {
																	private int ticks = 0;

																	public void startDelay(LevelAccessor world) {
																		ServerTickEvents.END_SERVER_TICK.register((server) -> {
																			this.ticks++;
																			if (this.ticks == 40) {
																				Mob _entity = (Mob) entityiterator;
																				_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																				new Object() {
																					private int ticks = 0;

																					public void startDelay(LevelAccessor world) {
																						ServerTickEvents.END_SERVER_TICK.register((server) -> {
																							this.ticks++;
																							if (this.ticks == 40) {
																								Mob _entity = (Mob) entityiterator;
																								_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																								new Object() {
																									private int ticks = 0;

																									public void startDelay(LevelAccessor world) {
																										ServerTickEvents.END_SERVER_TICK.register((server) -> {
																											this.ticks++;
																											if (this.ticks == 40) {
																												Mob _entity = (Mob) entityiterator;
																												_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																												new Object() {
																													private int ticks = 0;

																													public void startDelay(LevelAccessor world) {
																														ServerTickEvents.END_SERVER_TICK.register((server) -> {
																															this.ticks++;
																															if (this.ticks == 40) {
																																Mob _entity = (Mob) entityiterator;
																																_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																																new Object() {
																																	private int ticks = 0;

																																	public void startDelay(LevelAccessor world) {
																																		ServerTickEvents.END_SERVER_TICK.register((server) -> {
																																			this.ticks++;
																																			if (this.ticks == 40) {
																																				Mob _entity = (Mob) entityiterator;
																																				_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																																				new Object() {
																																					private int ticks = 0;

																																					public void startDelay(LevelAccessor ignoredWorld) {
																																						ServerTickEvents.END_SERVER_TICK.register((server) -> {
																																							this.ticks++;
																																							if (this.ticks == 40) {
																																								Mob _entity = (Mob) entityiterator;
																																								_entity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																																							}
																																						});
																																					}
																																				}.startDelay(world);
																																			}
																																		});
																																	}
																																}.startDelay(world);
																															}
																														});
																													}
																												}.startDelay(world);
																											}
																										});
																									}
																								}.startDelay(world);
																							}
																						});
																					}
																				}.startDelay(world);
																			}
																		});
																	}
																}.startDelay(world);
															}
														});
													}
												}.startDelay(world);
											}

										}
									}
								}
							}
						}
				} else if (entity.isPassenger()) {
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal((Component.translatable("translation.spur.on").getString())), true);
					if (!((entity.getRootVehicle()) instanceof LivingEntity _livEnt72 && _livEnt72.hasEffect(MobEffects.MOVEMENT_SPEED))) {
						if ((entity.getRootVehicle()) instanceof LivingEntity _entity)
							_entity.hurt(new DamageSource(_entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)) {
								@Override
								public @NotNull Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
									return Component.translatable("death.attack." + "spur");
								}
							}, 1);
						if ((entity.getRootVehicle()) instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
					} else if ((entity.getRootVehicle()) instanceof LivingEntity _livEnt78 && _livEnt78.hasEffect(MobEffects.MOVEMENT_SPEED)) {
						if ((entity.getRootVehicle()) instanceof LivingEntity _entity)
							_entity.hurt(new DamageSource(_entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)) {
								@Override
								public @NotNull Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
									return Component.translatable("death.attack." + "spur");
								}
							}, 2);
						if ((entity.getRootVehicle()) instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false));
					}
				}
			}
		}
	}
}
