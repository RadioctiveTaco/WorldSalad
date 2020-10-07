package com.radioctivetacoo.worldsalad.world.biomes;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;
import com.radioctivetacoo.worldsalad.init.BlockInit;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class MushroomCanyonFlatsBiomeSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
	private static final BlockState CLAYSTONE = BlockInit.CLAYSTONE.get().getDefaultState();
	private static final BlockState CLAYSTONE2 = BlockInit.CLAYSTONE.get().getDefaultState();
	private static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
	private static final BlockState PURPLE_CLAYSTONE = BlockInit.PURPLE_CLAYSTONE.get().getDefaultState();
	private static final BlockState GREEN_CLAYSTONE = BlockInit.GREEN_CLAYSTONE.get().getDefaultState();
	private static final BlockState RED_CLAYSTONE = BlockInit.RED_CLAYSTONE.get().getDefaultState();
	private static final BlockState WHITE_CLAYSTONE = BlockInit.WHITE_CLAYSTONE.get().getDefaultState();
	protected BlockState[] field_215432_a;
	protected long field_215433_b;
	protected PerlinNoiseGenerator field_215435_c;
	protected PerlinNoiseGenerator field_215437_d;
	protected PerlinNoiseGenerator field_215439_e;

	public MushroomCanyonFlatsBiomeSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> func) {
		super(func);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise,
			BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		int i = x & 15;
		int j = z & 15;
		BlockState blockstate = CLAYSTONE;
		@SuppressWarnings("unused")
		BlockState blockstate1 = biomeIn.getSurfaceBuilderConfig().getUnder();
		int k = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
		boolean flag = Math.cos(noise / 3.0D * Math.PI) > 0.0D;
		int l = -1;
		boolean flag1 = false;
		int i1 = 0;
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for (int j1 = startHeight; j1 >= 0; --j1) {
			if (i1 < 15) {
				blockpos$mutable.setPos(i, j1, j);
				BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);
				if (blockstate2.isAir()) {
					l = -1;
				} else if (blockstate2.getBlock() == defaultBlock.getBlock()) {
					if (l == -1) {
						flag1 = false;
						if (k <= 0) {
							blockstate = Blocks.AIR.getDefaultState();
							blockstate1 = defaultBlock;
						} else if (j1 >= seaLevel - 4 && j1 <= seaLevel + 1) {
							blockstate = CLAYSTONE;
							blockstate1 = biomeIn.getSurfaceBuilderConfig().getUnder();
						}

						if (j1 < seaLevel && (blockstate == null || blockstate.isAir())) {
							blockstate = defaultFluid;
						}

						l = k + Math.max(0, j1 - seaLevel);
						if (j1 >= seaLevel - 1) {
							if (j1 > seaLevel + 3 + k) {
								BlockState blockstate3;
								if (j1 >= 64 && j1 <= 127) {
									if (flag) {
										blockstate3 = RED_SAND;
									} else {
										blockstate3 = this.func_215431_a(x, j1, z);
									}
								} else {
									blockstate3 = CLAYSTONE2;
								}

								chunkIn.setBlockState(blockpos$mutable, blockstate3, false);
							} else {
								chunkIn.setBlockState(blockpos$mutable, biomeIn.getSurfaceBuilderConfig().getTop(),
										false);
								flag1 = true;
							}
						} else {
						}
					} else if (l > 0) {
						--l;
						if (flag1) {
							chunkIn.setBlockState(blockpos$mutable, CLAYSTONE2, false);
						} else {
							chunkIn.setBlockState(blockpos$mutable, this.func_215431_a(x, j1, z), false);
						}
					}

					++i1;
				}
			}
		}

	}

	@Override
	public void setSeed(long seed) {
		if (this.field_215433_b != seed || this.field_215432_a == null) {
			this.func_215430_b(seed);
		}

		if (this.field_215433_b != seed || this.field_215435_c == null || this.field_215437_d == null) {
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
			this.field_215435_c = new PerlinNoiseGenerator(sharedseedrandom, 3, 0);
			this.field_215437_d = new PerlinNoiseGenerator(sharedseedrandom, 0, 0);
		}

		this.field_215433_b = seed;
	}

	protected void func_215430_b(long p_215430_1_) {
		this.field_215432_a = new BlockState[64];
		Arrays.fill(this.field_215432_a, RED_SAND);
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_215430_1_);
		this.field_215439_e = new PerlinNoiseGenerator(sharedseedrandom, 0, 0);

		for (int l1 = 0; l1 < 64; ++l1) {
			l1 += sharedseedrandom.nextInt(5) + 1;
			if (l1 < 64) {
				this.field_215432_a[l1] = CLAYSTONE2;
			}
		}

		int i2 = sharedseedrandom.nextInt(4) + 2;

		for (int i = 0; i < i2; ++i) {
			int j = sharedseedrandom.nextInt(3) + 1;
			int k = sharedseedrandom.nextInt(64);

			for (int l = 0; k + l < 64 && l < j; ++l) {
				this.field_215432_a[k + l] = PURPLE_CLAYSTONE;
			}
		}

		int j2 = sharedseedrandom.nextInt(4) + 2;

		for (int k2 = 0; k2 < j2; ++k2) {
			int i3 = sharedseedrandom.nextInt(3) + 2;
			int l3 = sharedseedrandom.nextInt(64);

			for (int i1 = 0; l3 + i1 < 64 && i1 < i3; ++i1) {
				this.field_215432_a[l3 + i1] = GREEN_CLAYSTONE;
			}
		}

		int l2 = sharedseedrandom.nextInt(4) + 2;

		for (int j3 = 0; j3 < l2; ++j3) {
			int i4 = sharedseedrandom.nextInt(3) + 1;
			int k4 = sharedseedrandom.nextInt(64);

			for (int j1 = 0; k4 + j1 < 64 && j1 < i4; ++j1) {
				this.field_215432_a[k4 + j1] = RED_CLAYSTONE;
			}
		}

		int k3 = sharedseedrandom.nextInt(3) + 3;
		int j4 = 0;

		for (int l4 = 0; l4 < k3; ++l4) {
			j4 += sharedseedrandom.nextInt(16) + 4;

			for (int k1 = 0; j4 + k1 < 64 && k1 < 1; ++k1) {
				this.field_215432_a[j4 + k1] = CLAYSTONE;
				if (j4 + k1 > 1 && sharedseedrandom.nextBoolean()) {
					this.field_215432_a[j4 + k1 - 1] = WHITE_CLAYSTONE;
				}

				if (j4 + k1 < 63 && sharedseedrandom.nextBoolean()) {
					this.field_215432_a[j4 + k1 + 1] = WHITE_CLAYSTONE;
				}
			}
		}

	}

	protected BlockState func_215431_a(int p_215431_1_, int p_215431_2_, int p_215431_3_) {
		int i = (int) Math
				.round(this.field_215439_e.noiseAt(p_215431_1_ / 512.0D, p_215431_3_ / 512.0D, false)
						* 2.0D);
		return this.field_215432_a[(p_215431_2_ + i + 64) % 64];
	}
}
