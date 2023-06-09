package net.nussi.dedicated_applied_energistics.init;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import net.minecraft.resources.ResourceLocation;
import net.nussi.dedicated_applied_energistics.items.InterDimensionalStorageCellHandler;

public class CellInit {

    public static void Init() {
        StorageCells.addCellHandler(new InterDimensionalStorageCellHandler());

        StorageCellModels.registerModel(ItemInit.INTER_DIMENSIONAL_STORAGE_CELL_ITEM.get(), new ResourceLocation("dae2:block/drive/cells/inter_dimensional_storage_cell"));
    }

}
