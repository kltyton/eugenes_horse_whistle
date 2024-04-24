package com.kltyton.eugeneshorsewhistle.whistle;

import com.kltyton.eugeneshorsewhistle.Config.ModConfig;
import com.kltyton.eugeneshorsewhistle.init.EugenesHorseWhistleModSounds;
import com.kltyton.eugeneshorsewhistle.item.ModItems;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class UseWhistleAndSpur {
	private static final double ENTITY_SEARCH_RADIUS = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getEntitySearchRadius();
	private static final int MAX_TELEPORT_OFFSET = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getMaxTeleportOffset();
	private static final double TELEPORT_DISTANCE_THRESHOLD = AutoConfig.getConfigHolder(ModConfig.class).getConfig().getTeleportDistanceThreshold();
	private static final boolean PLAYER_WHISTLE_SOUND = AutoConfig.getConfigHolder(ModConfig.class).getConfig().shouldPlayWhistleSound();
	private static final boolean SPURS = AutoConfig.getConfigHolder(ModConfig.class).getConfig().isSpurs();

	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof ServerLevel _level) {
			float pitch;
			pitch = 0.5f + (new Random().nextFloat());
			if (PLAYER_WHISTLE_SOUND) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), EugenesHorseWhistleModSounds.WHISTLE, SoundSource.PLAYERS, 1, pitch);
					if (_level.getServer() != null) {
						_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, entity.position(), entity.getRotationVector(), _level, 4,
								entity.getName().getString(), entity.getDisplayName(), _level.getServer(), entity), "cpm animate @s whistle");
					} else {
						_level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), EugenesHorseWhistleModSounds.WHISTLE, SoundSource.PLAYERS, 1, pitch, false);
					}
				}
			}
			boolean found =false;
			final Vec3 playerCenter = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            for (AbstractHorse tamedEntity : _level.getEntitiesOfClass(AbstractHorse.class, new AABB(playerCenter, playerCenter).inflate(ENTITY_SEARCH_RADIUS), e -> e instanceof AbstractHorse && e.isTamed() && Objects.equals(e.getOwnerUUID(), entity.getUUID()))) {
				boolean hasEnderHorseArmor = false;
				ItemStack armor = tamedEntity.getItemBySlot(EquipmentSlot.CHEST); // 获取马当前装备的盔甲
				if (!armor.isEmpty() && armor.getItem() == ModItems.ENDER_HORSE_ARMOR) {
					hasEnderHorseArmor = true;
					if (!tamedEntity.isVehicle()) {
						tamedEntity.remove(Entity.RemovalReason.DISCARDED);
					}
				}
				if (!tamedEntity.isVehicle()) {
					tamedEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 1, false, false));
					found = true;
						// 向实体添加效果
					if (!tamedEntity.isLeashed()) {
						double distance = tamedEntity.position().distanceTo(playerCenter);
							// 计算马与主人的距离
						if (distance > TELEPORT_DISTANCE_THRESHOLD) {
							Random random = new Random();
							int maxOffset = MAX_TELEPORT_OFFSET;
							int offsetX = random.nextInt(maxOffset * 2 + 1) - maxOffset;
							int offsetZ = random.nextInt(maxOffset * 2 + 1) - maxOffset;
							MinecraftServer server = world.getServer();
							ServerPlayer player;
							player = Objects.requireNonNull(server).getPlayerList().getPlayer(entity.getUUID());
							assert player != null;
							BlockPos ownerPos = player.blockPosition();
							BlockPos targetPos = new BlockPos(ownerPos.getX() + offsetX, ownerPos.getY(), ownerPos.getZ() + offsetZ);
							// 尝试在目标位置生成传送点
							int maxAttempts = 20;
							int attempts = 0;
							while (!_level.getBlockState(targetPos).isAir() && attempts < maxAttempts) {
								offsetX = random.nextInt(maxOffset * 2 + 1) - maxOffset;
								offsetZ = random.nextInt(maxOffset * 2 + 1) - maxOffset;
								targetPos = new BlockPos(ownerPos.getX() + offsetX, ownerPos.getY(), ownerPos.getZ() + offsetZ);
								attempts++;
							}
							if (attempts == maxAttempts) {
								if (entity instanceof Player _player && !_player.level().isClientSide()) {
									_player.displayClientMessage(Component.literal((Component.translatable("translation.tip.place.inappropriate").getString())), false);
								}
							}else if (entity instanceof Player _player && !_player.level().isClientSide()) {
								_player.displayClientMessage(Component.literal((Component.translatable("translation.tip.coming").getString())), false);
							}
							// 将马传送至目标位置
							tamedEntity.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
							float previousHealth = tamedEntity.getHealth();
							// 如果传送前后马的生命值有变化，将马传送回主人位置
							if (previousHealth > 0 && tamedEntity.getHealth() < previousHealth) {
								tamedEntity.teleportTo(ownerPos.getX(), ownerPos.getY(), ownerPos.getZ());
								tamedEntity.getNavigation().stop();
							}
							tamedEntity.getNavigation().stop();
						}
					}
				} else if (SPURS) {
					if (entity.isPassenger()) {
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
                if (!hasEnderHorseArmor) {
					if (tamedEntity instanceof Horse) {
						new Object() {
							private int ticks = 0;

							public void startDelay(LevelAccessor world) {
								ServerTickEvents.END_SERVER_TICK.register((server) -> {
									this.ticks++;
									if (this.ticks == 30) {
										if (world instanceof Level _level) {
											if (!_level.isClientSide()) {
												_level.playSound(null, BlockPos.containing(tamedEntity.getX(), tamedEntity.getY(), tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.horse.angry")), SoundSource.AMBIENT, 5, 1);
											} else {
												_level.playLocalSound((tamedEntity.getX()), (tamedEntity.getY()), (tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.horse.angry")), SoundSource.AMBIENT, 5, 1, false);
											}
										}
									}
								});
							}
						}.startDelay(world);
					}
					if (tamedEntity instanceof Donkey) {
						new Object() {
							private int ticks = 0;

							public void startDelay(LevelAccessor world) {
								ServerTickEvents.END_SERVER_TICK.register((server) -> {
									this.ticks++;
									if (this.ticks == 30) {
										if (world instanceof Level _level) {
											if (!_level.isClientSide()) {
												_level.playSound(null, BlockPos.containing(tamedEntity.getX(), tamedEntity.getY(), tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.donkey.angry")), SoundSource.AMBIENT, 5, 1);
											} else {
												_level.playLocalSound((tamedEntity.getX()), (tamedEntity.getY()), (tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.donkey.angry")), SoundSource.AMBIENT, 5, 1, false);
											}
										}
									}
								});
							}
						}.startDelay(world);
					}
					if (tamedEntity instanceof Mule) {
						new Object() {
							private int ticks = 0;

							public void startDelay(LevelAccessor world) {
								ServerTickEvents.END_SERVER_TICK.register((server) -> {
									this.ticks++;
									if (this.ticks == 30) {
										if (world instanceof Level _level) {
											if (!_level.isClientSide()) {
												_level.playSound(null, BlockPos.containing(tamedEntity.getX(), tamedEntity.getY(), tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.mule.angry")), SoundSource.AMBIENT, 5, 1);
											} else {
												_level.playLocalSound((tamedEntity.getX()), (tamedEntity.getY()), (tamedEntity.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("entity.mule.angry")), SoundSource.AMBIENT, 5, 1, false);
											}
										}
									}
								});
							}
						}.startDelay(world);
					}
				}
				new Object() {
					private int ticks = 0;
					public void startDelay(LevelAccessor world) {
						ServerTickEvents.END_SERVER_TICK.register((server) -> {
							this.ticks++;
							if (this.ticks == 40) {
								tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
								new Object() {
									private int ticks = 0;
									public void startDelay(LevelAccessor world) {
										ServerTickEvents.END_SERVER_TICK.register((server) -> {
											this.ticks++;
											if (this.ticks == 40) {
												tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
												new Object() {
													private int ticks = 0;

													public void startDelay(LevelAccessor world) {
														ServerTickEvents.END_SERVER_TICK.register((server) -> {
															this.ticks++;
															if (this.ticks == 40) {
																tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																new Object() {
																	private int ticks = 0;

																	public void startDelay(LevelAccessor world) {
																		ServerTickEvents.END_SERVER_TICK.register((server) -> {
																			this.ticks++;
																			if (this.ticks == 40) {
																				tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																				new Object() {
																					private int ticks = 0;

																					public void startDelay(LevelAccessor world) {
																						ServerTickEvents.END_SERVER_TICK.register((server) -> {
																							this.ticks++;
																							if (this.ticks == 40) {
																								tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																								new Object() {
																									private int ticks = 0;

																									public void startDelay(LevelAccessor world) {
																										ServerTickEvents.END_SERVER_TICK.register((server) -> {
																											this.ticks++;
																											if (this.ticks == 40) {
																												tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
																												new Object() {
																													private int ticks = 0;

																													public void startDelay(LevelAccessor ignoredWorld) {
																														ServerTickEvents.END_SERVER_TICK.register((server) -> {
																															this.ticks++;
																															if (this.ticks == 40) {
																																tamedEntity.getNavigation().moveTo((entity.getX()), (entity.getY()), (entity.getZ()), 2);
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
			// 获取保存的马实体的NBT数据列表
			boolean found2 = false;
			for (byte[] nbtData : HorseDiscardedEvent.getHorseNbtData().getNbtDataList()) {
				try {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(nbtData);
					NbtAccounter nbtAccounter = new NbtAccounter(2097152L, 512);
					CompoundTag horseTag = NbtIo.readCompressed(inputStream, nbtAccounter);
					AbstractHorse tamedEntity = EntityType.HORSE.create(_level);
					if (tamedEntity != null) {
						tamedEntity.load(horseTag);
						tamedEntity.setPos(playerCenter.x, playerCenter.y, playerCenter.z);
						_level.addFreshEntity(tamedEntity);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				found2 = true;
			}
			if (!found && !found2) {
				if (entity instanceof Player _player && !_player.level().isClientSide()) {
					_player.displayClientMessage(Component.literal((Component.translatable("translation.tip.not.found").getString())), false);
				}
			}
		}
	}
}