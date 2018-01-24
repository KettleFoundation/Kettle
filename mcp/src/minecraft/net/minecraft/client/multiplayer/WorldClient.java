package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World
{
    /** The packets that need to be sent to the server. */
    private final NetHandlerPlayClient connection;

    /** The ChunkProviderClient instance */
    private ChunkProviderClient clientChunkProvider;
    private final Set<Entity> entityList = Sets.<Entity>newHashSet();
    private final Set<Entity> entitySpawnQueue = Sets.<Entity>newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<ChunkPos> previousActiveChunkSet = Sets.<ChunkPos>newHashSet();
    private int ambienceTicks;
    protected Set<ChunkPos> visibleChunks;

    public WorldClient(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn)
    {
        super(new SaveHandlerMP(), new WorldInfo(settings, "MpServer"), DimensionType.getById(dimension).createDimension(), profilerIn, true);
        this.ambienceTicks = this.rand.nextInt(12000);
        this.visibleChunks = Sets.<ChunkPos>newHashSet();
        this.connection = netHandler;
        this.getWorldInfo().setDifficulty(difficulty);
        this.setSpawnPoint(new BlockPos(8, 64, 8));
        this.provider.setWorld(this);
        this.chunkProvider = this.createChunkProvider();
        this.mapStorage = new SaveDataMemoryStorage();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getBoolean("doDaylightCycle"))
        {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.profiler.startSection("reEntryProcessing");

        for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i)
        {
            Entity entity = this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);

            if (!this.loadedEntityList.contains(entity))
            {
                this.spawnEntity(entity);
            }
        }

        this.profiler.endStartSection("chunkCache");
        this.clientChunkProvider.tick();
        this.profiler.endStartSection("blocks");
        this.updateBlocks();
        this.profiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2)
    {
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return allowEmpty || !this.getChunkProvider().provideChunk(x, z).isEmpty();
    }

    protected void refreshVisibleChunks()
    {
        this.visibleChunks.clear();
        int i = this.mc.gameSettings.renderDistanceChunks;
        this.profiler.startSection("buildList");
        int j = MathHelper.floor(this.mc.player.posX / 16.0D);
        int k = MathHelper.floor(this.mc.player.posZ / 16.0D);

        for (int l = -i; l <= i; ++l)
        {
            for (int i1 = -i; i1 <= i; ++i1)
            {
                this.visibleChunks.add(new ChunkPos(l + j, i1 + k));
            }
        }

        this.profiler.endSection();
    }

    protected void updateBlocks()
    {
        this.refreshVisibleChunks();

        if (this.ambienceTicks > 0)
        {
            --this.ambienceTicks;
        }

        this.previousActiveChunkSet.retainAll(this.visibleChunks);

        if (this.previousActiveChunkSet.size() == this.visibleChunks.size())
        {
            this.previousActiveChunkSet.clear();
        }

        int i = 0;

        for (ChunkPos chunkpos : this.visibleChunks)
        {
            if (!this.previousActiveChunkSet.contains(chunkpos))
            {
                int j = chunkpos.x * 16;
                int k = chunkpos.z * 16;
                this.profiler.startSection("getChunk");
                Chunk chunk = this.getChunkFromChunkCoords(chunkpos.x, chunkpos.z);
                this.playMoodSoundAndCheckLight(j, k, chunk);
                this.profiler.endSection();
                this.previousActiveChunkSet.add(chunkpos);
                ++i;

                if (i >= 10)
                {
                    return;
                }
            }
        }
    }

    public void doPreChunk(int chunkX, int chunkZ, boolean loadChunk)
    {
        if (loadChunk)
        {
            this.clientChunkProvider.loadChunk(chunkX, chunkZ);
        }
        else
        {
            this.clientChunkProvider.unloadChunk(chunkX, chunkZ);
            this.markBlockRangeForRenderUpdate(chunkX * 16, 0, chunkZ * 16, chunkX * 16 + 15, 256, chunkZ * 16 + 15);
        }
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntity(Entity entityIn)
    {
        boolean flag = super.spawnEntity(entityIn);
        this.entityList.add(entityIn);

        if (flag)
        {
            if (entityIn instanceof EntityMinecart)
            {
                this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
            }
        }
        else
        {
            this.entitySpawnQueue.add(entityIn);
        }

        return flag;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity entityIn)
    {
        super.removeEntity(entityIn);
        this.entityList.remove(entityIn);
    }

    protected void onEntityAdded(Entity entityIn)
    {
        super.onEntityAdded(entityIn);

        if (this.entitySpawnQueue.contains(entityIn))
        {
            this.entitySpawnQueue.remove(entityIn);
        }
    }

    protected void onEntityRemoved(Entity entityIn)
    {
        super.onEntityRemoved(entityIn);

        if (this.entityList.contains(entityIn))
        {
            if (entityIn.isEntityAlive())
            {
                this.entitySpawnQueue.add(entityIn);
            }
            else
            {
                this.entityList.remove(entityIn);
            }
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(int entityID, Entity entityToSpawn)
    {
        Entity entity = this.getEntityByID(entityID);

        if (entity != null)
        {
            this.removeEntity(entity);
        }

        this.entityList.add(entityToSpawn);
        entityToSpawn.setEntityId(entityID);

        if (!this.spawnEntity(entityToSpawn))
        {
            this.entitySpawnQueue.add(entityToSpawn);
        }

        this.entitiesById.addKey(entityID, entityToSpawn);
    }

    @Nullable

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int id)
    {
        return (Entity)(id == this.mc.player.getEntityId() ? this.mc.player : super.getEntityByID(id));
    }

    public Entity removeEntityFromWorld(int entityID)
    {
        Entity entity = this.entitiesById.removeObject(entityID);

        if (entity != null)
        {
            this.entityList.remove(entity);
            this.removeEntity(entity);
        }

        return entity;
    }

    @Deprecated
    public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        this.invalidateBlockReceiveRegion(i, j, k, i, j, k);
        return super.setBlockState(pos, state, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.connection.getNetworkManager().closeChannel(new TextComponentString("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
    }

    protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn)
    {
        super.playMoodSoundAndCheckLight(x, z, chunkIn);

        if (this.ambienceTicks == 0)
        {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int i = this.updateLCG >> 2;
            int j = i & 15;
            int k = i >> 8 & 15;
            int l = i >> 16 & 255;
            BlockPos blockpos = new BlockPos(j + x, l, k + z);
            IBlockState iblockstate = chunkIn.getBlockState(blockpos);
            j = j + x;
            k = k + z;

            if (iblockstate.getMaterial() == Material.AIR && this.getLight(blockpos) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, blockpos) <= 0)
            {
                double d0 = this.mc.player.getDistanceSq((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D);

                if (this.mc.player != null && d0 > 4.0D && d0 < 256.0D)
                {
                    this.playSound((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D, SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.rand.nextFloat() * 0.2F, false);
                    this.ambienceTicks = this.rand.nextInt(12000) + 6000;
                }
            }
        }
    }

    public void doVoidFogParticles(int posX, int posY, int posZ)
    {
        int i = 32;
        Random random = new Random();
        ItemStack itemstack = this.mc.player.getHeldItemMainhand();
        boolean flag = this.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock(Blocks.BARRIER);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j = 0; j < 667; ++j)
        {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }
    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos)
    {
        int i = x + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        int j = y + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        int k = z + this.rand.nextInt(offset) - this.rand.nextInt(offset);
        pos.setPos(i, j, k);
        IBlockState iblockstate = this.getBlockState(pos);
        iblockstate.getBlock().randomDisplayTick(iblockstate, this, pos, random);

        if (holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER)
        {
            this.spawnParticle(EnumParticleTypes.BARRIER, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities()
    {
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int i = 0; i < this.unloadedEntityList.size(); ++i)
        {
            Entity entity = this.unloadedEntityList.get(i);
            int j = entity.chunkCoordX;
            int k = entity.chunkCoordZ;

            if (entity.addedToChunk && this.isChunkLoaded(j, k, true))
            {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity);
            }
        }

        for (int i1 = 0; i1 < this.unloadedEntityList.size(); ++i1)
        {
            this.onEntityRemoved(this.unloadedEntityList.get(i1));
        }

        this.unloadedEntityList.clear();

        for (int j1 = 0; j1 < this.loadedEntityList.size(); ++j1)
        {
            Entity entity1 = this.loadedEntityList.get(j1);
            Entity entity2 = entity1.getRidingEntity();

            if (entity2 != null)
            {
                if (!entity2.isDead && entity2.isPassenger(entity1))
                {
                    continue;
                }

                entity1.dismountRidingEntity();
            }

            if (entity1.isDead)
            {
                int k1 = entity1.chunkCoordX;
                int l = entity1.chunkCoordZ;

                if (entity1.addedToChunk && this.isChunkLoaded(k1, l, true))
                {
                    this.getChunkFromChunkCoords(k1, l).removeEntity(entity1);
                }

                this.loadedEntityList.remove(j1--);
                this.onEntityRemoved(entity1);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report)
    {
        CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(report);
        crashreportcategory.addDetail("Forced entities", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList;
            }
        });
        crashreportcategory.addDetail("Retry entities", new ICrashReportDetail<String>()
        {
            public String call()
            {
                return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue;
            }
        });
        crashreportcategory.addDetail("Server brand", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return WorldClient.this.mc.player.getServerBrand();
            }
        });
        crashreportcategory.addDetail("Server type", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }
        });
        return crashreportcategory;
    }

    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        if (player == this.mc.player)
        {
            this.playSound(x, y, z, soundIn, category, volume, pitch, false);
        }
    }

    public void playSound(BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        this.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundIn, category, volume, pitch, distanceDelay);
    }

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        double d0 = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
        PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(soundIn, category, volume, pitch, (float)x, (float)y, (float)z);

        if (distanceDelay && d0 > 100.0D)
        {
            double d1 = Math.sqrt(d0) / 40.0D;
            this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int)(d1 * 20.0D));
        }
        else
        {
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound)
    {
        this.mc.effectRenderer.addEffect(new ParticleFirework.Starter(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compound));
    }

    public void sendPacketToServer(Packet<?> packetIn)
    {
        this.connection.sendPacket(packetIn);
    }

    public void setWorldScoreboard(Scoreboard scoreboardIn)
    {
        this.worldScoreboard = scoreboardIn;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long time)
    {
        if (time < 0L)
        {
            time = -time;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        }
        else
        {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(time);
    }

    /**
     * gets the world's chunk provider
     */
    public ChunkProviderClient getChunkProvider()
    {
        return (ChunkProviderClient)super.getChunkProvider();
    }
}
