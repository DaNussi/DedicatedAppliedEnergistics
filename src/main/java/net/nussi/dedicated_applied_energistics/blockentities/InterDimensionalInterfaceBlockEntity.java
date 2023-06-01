package net.nussi.dedicated_applied_energistics.blockentities;

import appeng.api.inventories.BaseInternalInventory;
import appeng.api.inventories.InternalInventory;
import appeng.blockentity.AEBaseInvBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.nussi.dedicated_applied_energistics.init.BlockEntityTypeInit;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class InterDimensionalInterfaceBlockEntity extends AEBaseInvBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final InternalInventory internalInventory = new TestInternalInventory();

    public InterDimensionalInterfaceBlockEntity(BlockPos pos, BlockState blockState) {
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

    public static class TestInternalInventory implements InternalInventory {
        HashMap<Integer, ItemStack> hash = new HashMap<>();

        @Override
        public int size() {
            LOGGER.info("Inv Size: " + hash.size() + 1);
            return hash.size() + 1;
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            ItemStack itemStack = hash.get(slotIndex);
            if(itemStack == null) return ItemStack.EMPTY;
            return itemStack;
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            hash.put(slotIndex, stack);
        }
    }

    public static class DedicatedInternalInventory extends BaseInternalInventory {
        private JedisPoolConfig poolConfig;
        private JedisPool jedisPool;
        private Jedis jedis;

        public DedicatedInternalInventory (String RedisHost) {
            poolConfig = new JedisPoolConfig();
            jedisPool = new JedisPool(poolConfig, RedisHost);
            jedis = jedisPool.getResource();
        }

        @Override
        public int size() {
            return Math.toIntExact(jedis.dbSize() + 1);
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            String data = jedis.get(calculateRedisKey(slotIndex));
            if(data == null) return ItemStack.EMPTY;
            CompoundTag compoundTag = deserializeCompoundTag(data);
            return ItemStack.of(compoundTag);
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            CompoundTag compoundTag = stack.serializeNBT();
            jedis.set(calculateRedisKey(slotIndex), serializeCompoundTag(compoundTag));
        }


        public static String calculateRedisKey(int slotIndex) {
            return "0.inv/" + slotIndex + ".slot";
        }


        public static String serializeCompoundTag(CompoundTag what){
            try {
                return what.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static CompoundTag deserializeCompoundTag(String data){
            try {
                return TagParser.parseTag(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
