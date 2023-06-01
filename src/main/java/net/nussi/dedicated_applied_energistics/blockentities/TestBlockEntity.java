package net.nussi.dedicated_applied_energistics.blockentities;

import appeng.api.inventories.BaseInternalInventory;
import appeng.api.inventories.InternalInventory;
import appeng.blockentity.AEBaseInvBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nussi.dedicated_applied_energistics.init.BlockEntityTypeInit;
import org.slf4j.Logger;

import java.util.HashMap;

public class TestBlockEntity extends AEBaseInvBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DedicatedInternalInventory internalInventory = new DedicatedInternalInventory();

    public TestBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypeInit.TEST_ENTITY_TYPE.get(), pos, blockState);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return internalInventory;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        LOGGER.info("onChangeInventory " + slot + " " + inv.toString());
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        // Disabled due to LAG when containing large amount of different items
    }

    @Override
    public void loadTag(CompoundTag data) {
        // Disabled due to LAG when containing large amount of different items
    }

    public static class DedicatedInternalInventory extends BaseInternalInventory {
        HashMap<Integer, ItemStack> hash = new HashMap<>();

        @Override
        public int size() {
            LOGGER.info("Inv Size: " + hash.size());
            return hash.size() + 1;
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            ItemStack out = hash.get(slotIndex);
            if(out == null) out = ItemStack.EMPTY;
            return out;
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            hash.put(slotIndex, stack);
        }
    }

}
