package com.radioctivetacoo.worldsalad.tileentity;

import com.radioctivetacoo.worldsalad.WorldSalad;
import com.radioctivetacoo.worldsalad.container.IndustrialGrinderContainer;
import com.radioctivetacoo.worldsalad.init.ItemInit;
import com.radioctivetacoo.worldsalad.init.RecipeSerializerInit;
import com.radioctivetacoo.worldsalad.init.TileEntityInit;
import com.radioctivetacoo.worldsalad.objects.blocks.machines.DeepFryerBlock;
import com.radioctivetacoo.worldsalad.recipes.IndustrialGrinderRecipe;
import com.radioctivetacoo.worldsalad.util.WorldSaladItemHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class IndustrialGrinderTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

	private ITextComponent customName;
	public int energyLeft = 0;
	public int energyMax = 1000;
	public int currentSmeltTime = 0;
	public final int maxSmeltTime = 150;
	private WorldSaladItemHandler inventory;

	public IndustrialGrinderTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);

		this.inventory = new WorldSaladItemHandler(4);
	}

	public IndustrialGrinderTileEntity() {
		this(TileEntityInit.INDUSTRIAL_GRINDER.get());
	}

	public boolean isPowered() {
		return this.energyLeft > 0;
	}
	
	public boolean isRunning() {
		return this.currentSmeltTime > 0;
	}

	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new IndustrialGrinderContainer(windowID, playerInv, this);
	}
	
	@Override
	public void tick() {
		boolean dirty = false;
		
		if (this.world != null && !this.world.isRemote) {
			if (this.inventory.getStackInSlot(0).isEmpty() || !isPowered()) {
				this.currentSmeltTime = 0;
			}
			if (this.inventory.getStackInSlot(3).getItem().equals(ItemInit.FLUX_POWDER.get()) && this.energyLeft + 200 <= this.energyMax) {
				this.inventory.decrStackSize(3, 1);
				this.energyLeft = this.energyLeft + 200;
			}
			if (this.isPowered() && this.getRecipe(this.inventory.getStackInSlot(0)) != null && this.inventory.getStackInSlot(1).getCount() < 64 && this.inventory.getStackInSlot(2).getCount() < 64) {
				ItemStack output = this.getRecipe(this.inventory.getStackInSlot(0)).getRecipeOutput();
				ItemStack outputBonus = this.getRecipe(this.inventory.getStackInSlot(0)).getOutputBonus();
				if ((this.inventory.getStackInSlot(1).getItem().equals(output.getItem()) || this.inventory.getStackInSlot(1).isEmpty()) && (this.inventory.getStackInSlot(2).getItem().equals(outputBonus.getItem()) || this.inventory.getStackInSlot(2).isEmpty())) {
				if (this.currentSmeltTime != this.maxSmeltTime) {
					this.energyLeft--;
					this.currentSmeltTime++;
					dirty = true;
					}
				}
			}
			if (this.isRunning()) {
				this.world.setBlockState(this.getPos(), this.getBlockState().with(DeepFryerBlock.LIT, true));
			}
			if (this.isRunning() == false) {
				this.world.setBlockState(this.getPos(), this.getBlockState().with(DeepFryerBlock.LIT, false));
			}
			if (this.currentSmeltTime == this.maxSmeltTime) {
				this.currentSmeltTime = 0;
				Random rand = new Random();
				ItemStack output = this.getRecipe(this.inventory.getStackInSlot(0)).getRecipeOutput();
				ItemStack outputBonus = this.getRecipe(this.inventory.getStackInSlot(0)).getOutputBonus();
				if (rand.nextInt(9) == 0) {
					this.inventory.insertItem(2, outputBonus.copy(), false);
				}
				this.inventory.insertItem(1, output.copy(), false);
				this.inventory.decrStackSize(0, 1);
				this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 0.7F, 0.8F);
				dirty = true;
			}

			if (dirty) {
				this.markDirty();
				this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(),
						Constants.BlockFlags.BLOCK_UPDATE);
			}
		}
	}

	public void setCustomName(ITextComponent name) {
		this.customName = name;
	}

	public ITextComponent getName() {
		return this.customName != null ? this.customName : this.getDefaultName();
	}

	private ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + WorldSalad.MOD_ID + ".industrial_grinder");
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.getName();
	}

	@Nullable
	public ITextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		if (compound.contains("CustomName", Constants.NBT.TAG_STRING)) {
			this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
		}

		NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(this.inventory.getSlots(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, inv);
		this.inventory.setNonNullList(inv);
		this.currentSmeltTime = compound.getInt("CurrentSmeltTime");
		this.energyLeft = compound.getInt("EnergyLeft");

	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (this.customName != null) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
		}

		ItemStackHelper.saveAllItems(compound, this.inventory.toNonNullList());
		compound.putInt("CurrentSmeltTime", this.currentSmeltTime);
		compound.putInt("EnergyLeft", this.energyLeft);

		return compound;
	}

	@Nullable
	private IndustrialGrinderRecipe getRecipe(ItemStack stack) {
		if (stack == null) {
			return null;
		}

		Set<IRecipe<?>> recipes = findRecipesByType(RecipeSerializerInit.INDUSTRIAL_GRINDER_TYPE, this.world);
		for (IRecipe<?> iRecipe : recipes) {
			IndustrialGrinderRecipe recipe = (IndustrialGrinderRecipe) iRecipe;
			if (recipe.matches(new RecipeWrapper(this.inventory), this.world)) {
				return recipe;
			}
		}

		return null;
	}

	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn, World world) {
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}

	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}

	public static Set<ItemStack> getAllRecipeInputs(IRecipeType<?> typeIn, World worldIn) {
		Set<ItemStack> inputs = new HashSet<ItemStack>();
		Set<IRecipe<?>> recipes = findRecipesByType(typeIn, worldIn);
		for (IRecipe<?> recipe : recipes) {
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			ingredients.forEach(ingredient -> {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					inputs.add(stack);
				}
			});
		}
		return inputs;
	}

	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		this.read(nbt);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
	}
}
