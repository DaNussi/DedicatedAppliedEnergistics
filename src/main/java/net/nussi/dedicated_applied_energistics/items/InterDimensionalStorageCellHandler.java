package net.nussi.dedicated_applied_energistics.items;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InterDimensionalStorageCellHandler implements ICellHandler {
    @Override
    public boolean isCell(ItemStack is) {
        return is.getItem() instanceof InterDimensionalStorageCell;
    }

    @Nullable
    @Override
    public StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        if(is.getItem() instanceof InterDimensionalStorageCell storageCell) {
            return storageCell;
        }
        return null;
    }
}
