package com.radioctivetacoo.worldsalad.world.biomes;

import com.radioctivetacoo.worldsalad.init.BlockInit;
import com.radioctivetacoo.worldsalad.world.gen.carvers.HyphaeAcidOceanCarver;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.HeightWithChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AcidOceanBiome extends Biome {
	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSkyColor() {
		return 0x26144f;
	}

	@Override
	public float getSpawningChance() {
		return 1F;
	}
	
	private static final BlockState TOADSTOOLBLOCK = BlockInit.TOADSTOOL.get().getDefaultState();
	private static final BlockState FUNGRASS = BlockInit.FUNGRASS.get().getDefaultState();
	private static final BlockState TALL_FUNGRASS = BlockInit.TALL_FUNGRASS.get().getDefaultState();
	public static final BlockClusterFeatureConfig TALL_FUNGRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(TALL_FUNGRASS), new DoublePlantBlockPlacer())).tries(164).func_227317_b_().build();
	BlockClusterFeatureConfig TOADSTOOL = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(TOADSTOOLBLOCK), new SimpleBlockPlacer())).tries(12).func_227317_b_().build();
	BlockClusterFeatureConfig FUNGRASS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(FUNGRASS), new SimpleBlockPlacer())).tries(32).build();
	
	public AcidOceanBiome(Builder biomeBuilder) {
		super(biomeBuilder);
		this.addCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(
				new HyphaeAcidOceanCarver(ProbabilityConfig::deserialize), new ProbabilityConfig(0.2f)));
		
		@SuppressWarnings("rawtypes")
		ConfiguredPlacement customConfig4 = Placement.COUNT_RANGE.configure(new CountRangeConfig(5, 30, 5, 50));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(
				OreFeatureConfig.FillerBlockType.create("MUD", null, new BlockMatcher(BlockInit.MUD.get())),
				BlockInit.GLOWING_CORAL.get().getDefaultState(), 40)).withPlacement(customConfig4));
		
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(TOADSTOOL).withPlacement(Placement.COUNT_CHANCE_HEIGHTMAP.configure(new HeightWithChanceConfig(9, 0.37F))));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
				Feature.RANDOM_PATCH.withConfiguration(FUNGRASS_CONFIG)
						.withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(5))));
		this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
				Feature.RANDOM_PATCH.withConfiguration(TALL_FUNGRASS_CONFIG)
						.withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(7))));
		
		@SuppressWarnings("rawtypes")
		ConfiguredPlacement customConfig0 = Placement.COUNT_RANGE.configure(new CountRangeConfig(150, 10, 5, 230));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
				Feature.ORE.withConfiguration(new OreFeatureConfig(
						OreFeatureConfig.FillerBlockType.create("DIRT", null,
								new BlockMatcher(Blocks.DIRT)),
						BlockInit.FUNGAL_DIRT.get().getDefaultState(), 100)).withPlacement(customConfig0));
		
		@SuppressWarnings("rawtypes")
		ConfiguredPlacement customConfigIslands = Placement.COUNT_RANGE.configure(new CountRangeConfig(150, 180, 0, 250));
		this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
				Feature.ORE.withConfiguration(new OreFeatureConfig(
						OreFeatureConfig.FillerBlockType.create("AIR", null,
								new BlockMatcher(Blocks.AIR)),
						BlockInit.FUNGAL_DIRT.get().getDefaultState(), 100)).withPlacement(customConfigIslands));
	
	}
}
