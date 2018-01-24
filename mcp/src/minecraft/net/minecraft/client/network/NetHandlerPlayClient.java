package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate;
import net.minecraft.client.renderer.debug.DebugRendererPathfinding;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The NetworkManager instance used to communicate with the server, used to respond to various packets (primarilly
     * movement and plugin channel related ones) and check the status of the network connection externally
     */
    private final NetworkManager netManager;
    private final GameProfile profile;

    /**
     * Seems to be either null (integrated server) or an instance of either GuiMultiplayer (when connecting to a server)
     * or GuiScreenReamlsTOS (when connecting to MCO server)
     */
    private final GuiScreen guiScreenServer;

    /**
     * Reference to the Minecraft instance, which many handler methods operate on
     */
    private Minecraft client;

    /**
     * Reference to the current ClientWorld instance, which many handler methods operate on
     */
    private WorldClient world;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of S08PacketPlayerPosLook,
     * reset upon respawning
     */
    private boolean doneLoadingTerrain;
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.<UUID, NetworkPlayerInfo>newHashMap();
    public int currentServerMaxPlayers = 20;
    private boolean hasStatistics;
    private final ClientAdvancementManager advancementManager;

    /**
     * Just an ordinary random number generator, used to randomize audio pitch of item/orb pickup and randomize both
     * particlespawn offset and velocity
     */
    private final Random avRandomizer = new Random();

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn, GameProfile profileIn)
    {
        this.client = mcIn;
        this.guiScreenServer = p_i46300_2_;
        this.netManager = networkManagerIn;
        this.profile = profileIn;
        this.advancementManager = new ClientAdvancementManager(mcIn);
    }

    /**
     * Clears the WorldClient instance associated with this NetHandlerPlayClient
     */
    public void cleanup()
    {
        this.world = null;
    }

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    public void handleJoinGame(SPacketJoinGame packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.playerController = new PlayerControllerMP(this.client, this);
        this.world = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), packetIn.getDifficulty(), this.client.mcProfiler);
        this.client.gameSettings.difficulty = packetIn.getDifficulty();
        this.client.loadWorld(this.world);
        this.client.player.dimension = packetIn.getDimension();
        this.client.displayGuiScreen(new GuiDownloadTerrain());
        this.client.player.setEntityId(packetIn.getPlayerId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.client.player.setReducedDebug(packetIn.isReducedDebugInfo());
        this.client.playerController.setGameType(packetIn.getGameType());
        this.client.gameSettings.sendSettingsToServer();
        this.netManager.sendPacket(new CPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
    }

    /**
     * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
     */
    public void handleSpawnObject(SPacketSpawnObject packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = null;

        if (packetIn.getType() == 10)
        {
            entity = EntityMinecart.create(this.world, d0, d1, d2, EntityMinecart.Type.getById(packetIn.getData()));
        }
        else if (packetIn.getType() == 90)
        {
            Entity entity1 = this.world.getEntityByID(packetIn.getData());

            if (entity1 instanceof EntityPlayer)
            {
                entity = new EntityFishHook(this.world, (EntityPlayer)entity1, d0, d1, d2);
            }

            packetIn.setData(0);
        }
        else if (packetIn.getType() == 60)
        {
            entity = new EntityTippedArrow(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 91)
        {
            entity = new EntitySpectralArrow(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 61)
        {
            entity = new EntitySnowball(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 68)
        {
            entity = new EntityLlamaSpit(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
        }
        else if (packetIn.getType() == 71)
        {
            entity = new EntityItemFrame(this.world, new BlockPos(d0, d1, d2), EnumFacing.getHorizontal(packetIn.getData()));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 77)
        {
            entity = new EntityLeashKnot(this.world, new BlockPos(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 65)
        {
            entity = new EntityEnderPearl(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 72)
        {
            entity = new EntityEnderEye(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 76)
        {
            entity = new EntityFireworkRocket(this.world, d0, d1, d2, ItemStack.EMPTY);
        }
        else if (packetIn.getType() == 63)
        {
            entity = new EntityLargeFireball(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 93)
        {
            entity = new EntityDragonFireball(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 64)
        {
            entity = new EntitySmallFireball(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 66)
        {
            entity = new EntityWitherSkull(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 67)
        {
            entity = new EntityShulkerBullet(this.world, d0, d1, d2, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 62)
        {
            entity = new EntityEgg(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 79)
        {
            entity = new EntityEvokerFangs(this.world, d0, d1, d2, 0.0F, 0, (EntityLivingBase)null);
        }
        else if (packetIn.getType() == 73)
        {
            entity = new EntityPotion(this.world, d0, d1, d2, ItemStack.EMPTY);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 75)
        {
            entity = new EntityExpBottle(this.world, d0, d1, d2);
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 1)
        {
            entity = new EntityBoat(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 50)
        {
            entity = new EntityTNTPrimed(this.world, d0, d1, d2, (EntityLivingBase)null);
        }
        else if (packetIn.getType() == 78)
        {
            entity = new EntityArmorStand(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 51)
        {
            entity = new EntityEnderCrystal(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 2)
        {
            entity = new EntityItem(this.world, d0, d1, d2);
        }
        else if (packetIn.getType() == 70)
        {
            entity = new EntityFallingBlock(this.world, d0, d1, d2, Block.getStateById(packetIn.getData() & 65535));
            packetIn.setData(0);
        }
        else if (packetIn.getType() == 3)
        {
            entity = new EntityAreaEffectCloud(this.world, d0, d1, d2);
        }

        if (entity != null)
        {
            EntityTracker.updateServerPosition(entity, d0, d1, d2);
            entity.rotationPitch = (float)(packetIn.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (float)(packetIn.getYaw() * 360) / 256.0F;
            Entity[] aentity = entity.getParts();

            if (aentity != null)
            {
                int i = packetIn.getEntityID() - entity.getEntityId();

                for (Entity entity2 : aentity)
                {
                    entity2.setEntityId(entity2.getEntityId() + i);
                }
            }

            entity.setEntityId(packetIn.getEntityID());
            entity.setUniqueId(packetIn.getUniqueId());
            this.world.addEntityToWorld(packetIn.getEntityID(), entity);

            if (packetIn.getData() > 0)
            {
                if (packetIn.getType() == 60 || packetIn.getType() == 91)
                {
                    Entity entity3 = this.world.getEntityByID(packetIn.getData() - 1);

                    if (entity3 instanceof EntityLivingBase && entity instanceof EntityArrow)
                    {
                        ((EntityArrow)entity).shootingEntity = entity3;
                    }
                }

                entity.setVelocity((double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            }
        }
    }

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = new EntityXPOrb(this.world, d0, d1, d2, packetIn.getXPValue());
        EntityTracker.updateServerPosition(entity, d0, d1, d2);
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.setEntityId(packetIn.getEntityID());
        this.world.addEntityToWorld(packetIn.getEntityID(), entity);
    }

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        Entity entity = null;

        if (packetIn.getType() == 1)
        {
            entity = new EntityLightningBolt(this.world, d0, d1, d2, false);
        }

        if (entity != null)
        {
            EntityTracker.updateServerPosition(entity, d0, d1, d2);
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packetIn.getEntityId());
            this.world.addWeatherEffect(entity);
        }
    }

    /**
     * Handles the spawning of a painting object
     */
    public void handleSpawnPainting(SPacketSpawnPainting packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPainting entitypainting = new EntityPainting(this.world, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
        entitypainting.setUniqueId(packetIn.getUniqueId());
        this.world.addEntityToWorld(packetIn.getEntityID(), entitypainting);
    }

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    public void handleEntityVelocity(SPacketEntityVelocity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            entity.setVelocity((double)packetIn.getMotionX() / 8000.0D, (double)packetIn.getMotionY() / 8000.0D, (double)packetIn.getMotionZ() / 8000.0D);
        }
    }

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    public void handleEntityMetadata(SPacketEntityMetadata packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity != null && packetIn.getDataManagerEntries() != null)
        {
            entity.getDataManager().setEntryValues(packetIn.getDataManagerEntries());
        }
    }

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    public void handleSpawnPlayer(SPacketSpawnPlayer packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = (float)(packetIn.getYaw() * 360) / 256.0F;
        float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.client.world, this.getPlayerInfo(packetIn.getUniqueId()).getGameProfile());
        entityotherplayermp.prevPosX = d0;
        entityotherplayermp.lastTickPosX = d0;
        entityotherplayermp.prevPosY = d1;
        entityotherplayermp.lastTickPosY = d1;
        entityotherplayermp.prevPosZ = d2;
        entityotherplayermp.lastTickPosZ = d2;
        EntityTracker.updateServerPosition(entityotherplayermp, d0, d1, d2);
        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.world.addEntityToWorld(packetIn.getEntityID(), entityotherplayermp);
        List < EntityDataManager.DataEntry<? >> list = packetIn.getDataManagerEntries();

        if (list != null)
        {
            entityotherplayermp.getDataManager().setEntryValues(list);
        }
    }

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    public void handleEntityTeleport(SPacketEntityTeleport packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity != null)
        {
            double d0 = packetIn.getX();
            double d1 = packetIn.getY();
            double d2 = packetIn.getZ();
            EntityTracker.updateServerPosition(entity, d0, d1, d2);

            if (!entity.canPassengerSteer())
            {
                float f = (float)(packetIn.getYaw() * 360) / 256.0F;
                float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;

                if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D)
                {
                    entity.setPositionAndRotationDirect(entity.posX, entity.posY, entity.posZ, f, f1, 0, true);
                }
                else
                {
                    entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
                }

                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    public void handleHeldItemChange(SPacketHeldItemChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (InventoryPlayer.isHotbar(packetIn.getHeldItemHotbarIndex()))
        {
            this.client.player.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
        }
    }

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    public void handleEntityMovement(SPacketEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            entity.serverPosX += (long)packetIn.getX();
            entity.serverPosY += (long)packetIn.getY();
            entity.serverPosZ += (long)packetIn.getZ();
            double d0 = (double)entity.serverPosX / 4096.0D;
            double d1 = (double)entity.serverPosY / 4096.0D;
            double d2 = (double)entity.serverPosZ / 4096.0D;

            if (!entity.canPassengerSteer())
            {
                float f = packetIn.isRotating() ? (float)(packetIn.getYaw() * 360) / 256.0F : entity.rotationYaw;
                float f1 = packetIn.isRotating() ? (float)(packetIn.getPitch() * 360) / 256.0F : entity.rotationPitch;
                entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, false);
                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    public void handleEntityHeadLook(SPacketEntityHeadLook packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            float f = (float)(packetIn.getYaw() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    public void handleDestroyEntities(SPacketDestroyEntities packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        for (int i = 0; i < packetIn.getEntityIDs().length; ++i)
        {
            this.world.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
        }
    }

    public void handlePlayerPosLook(SPacketPlayerPosLook packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer = this.client.player;
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = packetIn.getYaw();
        float f1 = packetIn.getPitch();

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
        {
            d0 += entityplayer.posX;
        }
        else
        {
            entityplayer.motionX = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
        {
            d1 += entityplayer.posY;
        }
        else
        {
            entityplayer.motionY = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
        {
            d2 += entityplayer.posZ;
        }
        else
        {
            entityplayer.motionZ = 0.0D;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
        {
            f1 += entityplayer.rotationPitch;
        }

        if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            f += entityplayer.rotationYaw;
        }

        entityplayer.setPositionAndRotation(d0, d1, d2, f, f1);
        this.netManager.sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
        this.netManager.sendPacket(new CPacketPlayer.PositionRotation(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

        if (!this.doneLoadingTerrain)
        {
            this.client.player.prevPosX = this.client.player.posX;
            this.client.player.prevPosY = this.client.player.posY;
            this.client.player.prevPosZ = this.client.player.posZ;
            this.doneLoadingTerrain = true;
            this.client.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketChunkData
     */
    public void handleMultiBlockChange(SPacketMultiBlockChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        for (SPacketMultiBlockChange.BlockUpdateData spacketmultiblockchange$blockupdatedata : packetIn.getChangedBlocks())
        {
            this.world.invalidateRegionAndSetBlock(spacketmultiblockchange$blockupdatedata.getPos(), spacketmultiblockchange$blockupdatedata.getBlockState());
        }
    }

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    public void handleChunkData(SPacketChunkData packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.isFullChunk())
        {
            this.world.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
        }

        this.world.invalidateBlockReceiveRegion(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
        Chunk chunk = this.world.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
        chunk.read(packetIn.getReadBuffer(), packetIn.getExtractedSize(), packetIn.isFullChunk());
        this.world.markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);

        if (!packetIn.isFullChunk() || !(this.world.provider instanceof WorldProviderSurface))
        {
            chunk.resetRelightChecks();
        }

        for (NBTTagCompound nbttagcompound : packetIn.getTileEntityTags())
        {
            BlockPos blockpos = new BlockPos(nbttagcompound.getInteger("x"), nbttagcompound.getInteger("y"), nbttagcompound.getInteger("z"));
            TileEntity tileentity = this.world.getTileEntity(blockpos);

            if (tileentity != null)
            {
                tileentity.readFromNBT(nbttagcompound);
            }
        }
    }

    public void processChunkUnload(SPacketUnloadChunk packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.world.doPreChunk(packetIn.getX(), packetIn.getZ(), false);
    }

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    public void handleBlockChange(SPacketBlockChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.world.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
    }

    /**
     * Closes the network channel
     */
    public void handleDisconnect(SPacketDisconnect packetIn)
    {
        this.netManager.closeChannel(packetIn.getReason());
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(ITextComponent reason)
    {
        this.client.loadWorld((WorldClient)null);

        if (this.guiScreenServer != null)
        {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy)
            {
                this.client.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).getProxy(), "disconnect.lost", reason)).getProxy());
            }
            else
            {
                this.client.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
            }
        }
        else
        {
            this.client.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
        }
    }

    public void sendPacket(Packet<?> packetIn)
    {
        this.netManager.sendPacket(packetIn);
    }

    public void handleCollectItem(SPacketCollectItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getCollectedItemEntityID());
        EntityLivingBase entitylivingbase = (EntityLivingBase)this.world.getEntityByID(packetIn.getEntityID());

        if (entitylivingbase == null)
        {
            entitylivingbase = this.client.player;
        }

        if (entity != null)
        {
            if (entity instanceof EntityXPOrb)
            {
                this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.35F + 0.9F, false);
            }
            else
            {
                this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 1.4F + 2.0F, false);
            }

            if (entity instanceof EntityItem)
            {
                ((EntityItem)entity).getItem().setCount(packetIn.getAmount());
            }

            this.client.effectRenderer.addEffect(new ParticleItemPickup(this.world, entity, entitylivingbase, 0.5F));
            this.world.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
        }
    }

    /**
     * Prints a chatmessage in the chat GUI
     */
    public void handleChat(SPacketChat packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.ingameGUI.addChatMessage(packetIn.getType(), packetIn.getChatComponent());
    }

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    public void handleAnimation(SPacketAnimation packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            if (packetIn.getAnimationType() == 0)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                entitylivingbase.swingArm(EnumHand.MAIN_HAND);
            }
            else if (packetIn.getAnimationType() == 3)
            {
                EntityLivingBase entitylivingbase1 = (EntityLivingBase)entity;
                entitylivingbase1.swingArm(EnumHand.OFF_HAND);
            }
            else if (packetIn.getAnimationType() == 1)
            {
                entity.performHurtAnimation();
            }
            else if (packetIn.getAnimationType() == 2)
            {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            }
            else if (packetIn.getAnimationType() == 4)
            {
                this.client.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
            }
            else if (packetIn.getAnimationType() == 5)
            {
                this.client.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
            }
        }
    }

    /**
     * Retrieves the player identified by the packet, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    public void handleUseBed(SPacketUseBed packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        packetIn.getPlayer(this.world).trySleep(packetIn.getBedPosition());
    }

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the packet
     */
    public void handleSpawnMob(SPacketSpawnMob packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = (float)(packetIn.getYaw() * 360) / 256.0F;
        float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityLivingBase entitylivingbase = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.client.world);

        if (entitylivingbase != null)
        {
            EntityTracker.updateServerPosition(entitylivingbase, d0, d1, d2);
            entitylivingbase.renderYawOffset = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
            entitylivingbase.rotationYawHead = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
            Entity[] aentity = entitylivingbase.getParts();

            if (aentity != null)
            {
                int i = packetIn.getEntityID() - entitylivingbase.getEntityId();

                for (Entity entity : aentity)
                {
                    entity.setEntityId(entity.getEntityId() + i);
                }
            }

            entitylivingbase.setEntityId(packetIn.getEntityID());
            entitylivingbase.setUniqueId(packetIn.getUniqueId());
            entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
            entitylivingbase.motionX = (double)((float)packetIn.getVelocityX() / 8000.0F);
            entitylivingbase.motionY = (double)((float)packetIn.getVelocityY() / 8000.0F);
            entitylivingbase.motionZ = (double)((float)packetIn.getVelocityZ() / 8000.0F);
            this.world.addEntityToWorld(packetIn.getEntityID(), entitylivingbase);
            List < EntityDataManager.DataEntry<? >> list = packetIn.getDataManagerEntries();

            if (list != null)
            {
                entitylivingbase.getDataManager().setEntryValues(list);
            }
        }
        else
        {
            LOGGER.warn("Skipping Entity with id {}", (int)packetIn.getEntityType());
        }
    }

    public void handleTimeUpdate(SPacketTimeUpdate packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.world.setTotalWorldTime(packetIn.getTotalWorldTime());
        this.client.world.setWorldTime(packetIn.getWorldTime());
    }

    public void handleSpawnPosition(SPacketSpawnPosition packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.player.setSpawnPoint(packetIn.getSpawnPos(), true);
        this.client.world.getWorldInfo().setSpawn(packetIn.getSpawnPos());
    }

    public void handleSetPassengers(SPacketSetPassengers packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity == null)
        {
            LOGGER.warn("Received passengers for unknown entity");
        }
        else
        {
            boolean flag = entity.isRidingOrBeingRiddenBy(this.client.player);
            entity.removePassengers();

            for (int i : packetIn.getPassengerIds())
            {
                Entity entity1 = this.world.getEntityByID(i);

                if (entity1 != null)
                {
                    entity1.startRiding(entity, true);

                    if (entity1 == this.client.player && !flag)
                    {
                        this.client.ingameGUI.setOverlayMessage(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(this.client.gameSettings.keyBindSneak.getKeyCode())), false);
                    }
                }
            }
        }
    }

    public void handleEntityAttach(SPacketEntityAttach packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());
        Entity entity1 = this.world.getEntityByID(packetIn.getVehicleEntityId());

        if (entity instanceof EntityLiving)
        {
            if (entity1 != null)
            {
                ((EntityLiving)entity).setLeashHolder(entity1, false);
            }
            else
            {
                ((EntityLiving)entity).clearLeashed(false, false);
            }
        }
    }

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    public void handleEntityStatus(SPacketEntityStatus packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            if (packetIn.getOpCode() == 21)
            {
                this.client.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
            }
            else if (packetIn.getOpCode() == 35)
            {
                int i = 40;
                this.client.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, 30);
                this.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);

                if (entity == this.client.player)
                {
                    this.client.entityRenderer.displayItemActivation(new ItemStack(Items.TOTEM_OF_UNDYING));
                }
            }
            else
            {
                entity.handleStatusUpdate(packetIn.getOpCode());
            }
        }
    }

    public void handleUpdateHealth(SPacketUpdateHealth packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.player.setPlayerSPHealth(packetIn.getHealth());
        this.client.player.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.client.player.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    public void handleSetExperience(SPacketSetExperience packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.player.setXPStats(packetIn.getExperienceBar(), packetIn.getTotalExperience(), packetIn.getLevel());
    }

    public void handleRespawn(SPacketRespawn packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.getDimensionID() != this.client.player.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.world.getScoreboard();
            this.world = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, this.client.world.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()), packetIn.getDimensionID(), packetIn.getDifficulty(), this.client.mcProfiler);
            this.world.setWorldScoreboard(scoreboard);
            this.client.loadWorld(this.world);
            this.client.player.dimension = packetIn.getDimensionID();
            this.client.displayGuiScreen(new GuiDownloadTerrain());
        }

        this.client.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
        this.client.playerController.setGameType(packetIn.getGameType());
    }

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    public void handleExplosion(SPacketExplosion packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Explosion explosion = new Explosion(this.client.world, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
        explosion.doExplosionB(true);
        this.client.player.motionX += (double)packetIn.getMotionX();
        this.client.player.motionY += (double)packetIn.getMotionY();
        this.client.player.motionZ += (double)packetIn.getMotionZ();
    }

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    public void handleOpenWindow(SPacketOpenWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayerSP entityplayersp = this.client.player;

        if ("minecraft:container".equals(packetIn.getGuiId()))
        {
            entityplayersp.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("minecraft:villager".equals(packetIn.getGuiId()))
        {
            entityplayersp.displayVillagerTradeGui(new NpcMerchant(entityplayersp, packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("EntityHorse".equals(packetIn.getGuiId()))
        {
            Entity entity = this.world.getEntityByID(packetIn.getEntityId());

            if (entity instanceof AbstractHorse)
            {
                entityplayersp.openGuiHorseInventory((AbstractHorse)entity, new ContainerHorseChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                entityplayersp.openContainer.windowId = packetIn.getWindowId();
            }
        }
        else if (!packetIn.hasSlots())
        {
            entityplayersp.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
        else
        {
            IInventory iinventory = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
            entityplayersp.displayGUIChest(iinventory);
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
    }

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    public void handleSetSlot(SPacketSetSlot packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer = this.client.player;
        ItemStack itemstack = packetIn.getStack();
        int i = packetIn.getSlot();
        this.client.getTutorial().handleSetSlot(itemstack);

        if (packetIn.getWindowId() == -1)
        {
            entityplayer.inventory.setItemStack(itemstack);
        }
        else if (packetIn.getWindowId() == -2)
        {
            entityplayer.inventory.setInventorySlotContents(i, itemstack);
        }
        else
        {
            boolean flag = false;

            if (this.client.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.client.currentScreen;
                flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.INVENTORY.getTabIndex();
            }

            if (packetIn.getWindowId() == 0 && packetIn.getSlot() >= 36 && i < 45)
            {
                if (!itemstack.isEmpty())
                {
                    ItemStack itemstack1 = entityplayer.inventoryContainer.getSlot(i).getStack();

                    if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount())
                    {
                        itemstack.setAnimationsToGo(5);
                    }
                }

                entityplayer.inventoryContainer.putStackInSlot(i, itemstack);
            }
            else if (packetIn.getWindowId() == entityplayer.openContainer.windowId && (packetIn.getWindowId() != 0 || !flag))
            {
                entityplayer.openContainer.putStackInSlot(i, itemstack);
            }
        }
    }

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    public void handleConfirmTransaction(SPacketConfirmTransaction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Container container = null;
        EntityPlayer entityplayer = this.client.player;

        if (packetIn.getWindowId() == 0)
        {
            container = entityplayer.inventoryContainer;
        }
        else if (packetIn.getWindowId() == entityplayer.openContainer.windowId)
        {
            container = entityplayer.openContainer;
        }

        if (container != null && !packetIn.wasAccepted())
        {
            this.sendPacket(new CPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
        }
    }

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    public void handleWindowItems(SPacketWindowItems packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer = this.client.player;

        if (packetIn.getWindowId() == 0)
        {
            entityplayer.inventoryContainer.setAll(packetIn.getItemStacks());
        }
        else if (packetIn.getWindowId() == entityplayer.openContainer.windowId)
        {
            entityplayer.openContainer.setAll(packetIn.getItemStacks());
        }
    }

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    public void handleSignEditorOpen(SPacketSignEditorOpen packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        TileEntity tileentity = this.world.getTileEntity(packetIn.getSignPosition());

        if (!(tileentity instanceof TileEntitySign))
        {
            tileentity = new TileEntitySign();
            tileentity.setWorld(this.world);
            tileentity.setPos(packetIn.getSignPosition());
        }

        this.client.player.openEditSign((TileEntitySign)tileentity);
    }

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    public void handleUpdateTileEntity(SPacketUpdateTileEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (this.client.world.isBlockLoaded(packetIn.getPos()))
        {
            TileEntity tileentity = this.client.world.getTileEntity(packetIn.getPos());
            int i = packetIn.getTileEntityType();
            boolean flag = i == 2 && tileentity instanceof TileEntityCommandBlock;

            if (i == 1 && tileentity instanceof TileEntityMobSpawner || flag || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner || i == 7 && tileentity instanceof TileEntityStructure || i == 8 && tileentity instanceof TileEntityEndGateway || i == 9 && tileentity instanceof TileEntitySign || i == 10 && tileentity instanceof TileEntityShulkerBox || i == 11 && tileentity instanceof TileEntityBed)
            {
                tileentity.readFromNBT(packetIn.getNbtCompound());
            }

            if (flag && this.client.currentScreen instanceof GuiCommandBlock)
            {
                ((GuiCommandBlock)this.client.currentScreen).updateGui();
            }
        }
    }

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    public void handleWindowProperty(SPacketWindowProperty packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer = this.client.player;

        if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == packetIn.getWindowId())
        {
            entityplayer.openContainer.updateProgressBar(packetIn.getProperty(), packetIn.getValue());
        }
    }

    public void handleEntityEquipment(SPacketEntityEquipment packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityID());

        if (entity != null)
        {
            entity.setItemStackToSlot(packetIn.getEquipmentSlot(), packetIn.getItemStack());
        }
    }

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    public void handleCloseWindow(SPacketCloseWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.player.closeScreenAndDropStack();
    }

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    public void handleBlockAction(SPacketBlockAction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.world.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    public void handleBlockBreakAnim(SPacketBlockBreakAnim packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.world.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
    }

    public void handleChangeGameState(SPacketChangeGameState packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer = this.client.player;
        int i = packetIn.getGameState();
        float f = packetIn.getValue();
        int j = MathHelper.floor(f + 0.5F);

        if (i >= 0 && i < SPacketChangeGameState.MESSAGE_NAMES.length && SPacketChangeGameState.MESSAGE_NAMES[i] != null)
        {
            entityplayer.sendStatusMessage(new TextComponentTranslation(SPacketChangeGameState.MESSAGE_NAMES[i], new Object[0]), false);
        }

        if (i == 1)
        {
            this.world.getWorldInfo().setRaining(true);
            this.world.setRainStrength(0.0F);
        }
        else if (i == 2)
        {
            this.world.getWorldInfo().setRaining(false);
            this.world.setRainStrength(1.0F);
        }
        else if (i == 3)
        {
            this.client.playerController.setGameType(GameType.getByID(j));
        }
        else if (i == 4)
        {
            if (j == 0)
            {
                this.client.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                this.client.displayGuiScreen(new GuiDownloadTerrain());
            }
            else if (j == 1)
            {
                this.client.displayGuiScreen(new GuiWinGame(true, () ->
                {
                    this.client.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                }));
            }
        }
        else if (i == 5)
        {
            GameSettings gamesettings = this.client.gameSettings;

            if (f == 0.0F)
            {
                this.client.displayGuiScreen(new GuiScreenDemo());
            }
            else if (f == 101.0F)
            {
                this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.movement", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindForward.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindLeft.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindBack.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindRight.getKeyCode())}));
            }
            else if (f == 102.0F)
            {
                this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindJump.getKeyCode())}));
            }
            else if (f == 103.0F)
            {
                this.client.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindInventory.getKeyCode())}));
            }
        }
        else if (i == 6)
        {
            this.world.playSound(entityplayer, entityplayer.posX, entityplayer.posY + (double)entityplayer.getEyeHeight(), entityplayer.posZ, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
        }
        else if (i == 7)
        {
            this.world.setRainStrength(f);
        }
        else if (i == 8)
        {
            this.world.setThunderStrength(f);
        }
        else if (i == 10)
        {
            this.world.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
            this.world.playSound(entityplayer, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
        }
    }

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    public void handleMaps(SPacketMaps packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        MapItemRenderer mapitemrenderer = this.client.entityRenderer.getMapItemRenderer();
        MapData mapdata = ItemMap.loadMapData(packetIn.getMapId(), this.client.world);

        if (mapdata == null)
        {
            String s = "map_" + packetIn.getMapId();
            mapdata = new MapData(s);

            if (mapitemrenderer.getMapInstanceIfExists(s) != null)
            {
                MapData mapdata1 = mapitemrenderer.getData(mapitemrenderer.getMapInstanceIfExists(s));

                if (mapdata1 != null)
                {
                    mapdata = mapdata1;
                }
            }

            this.client.world.setData(s, mapdata);
        }

        packetIn.setMapdataTo(mapdata);
        mapitemrenderer.updateMapTexture(mapdata);
    }

    public void handleEffect(SPacketEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.isSoundServerwide())
        {
            this.client.world.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
        else
        {
            this.client.world.playEvent(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
    }

    public void handleAdvancementInfo(SPacketAdvancementInfo packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.advancementManager.read(packetIn);
    }

    public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        ResourceLocation resourcelocation = packetIn.getTab();

        if (resourcelocation == null)
        {
            this.advancementManager.setSelectedTab((Advancement)null, false);
        }
        else
        {
            Advancement advancement = this.advancementManager.getAdvancementList().getAdvancement(resourcelocation);
            this.advancementManager.setSelectedTab(advancement, false);
        }
    }

    /**
     * Updates the players statistics or achievements
     */
    public void handleStatistics(SPacketStatistics packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        for (Entry<StatBase, Integer> entry : packetIn.getStatisticMap().entrySet())
        {
            StatBase statbase = entry.getKey();
            int k = ((Integer)entry.getValue()).intValue();
            this.client.player.getStatFileWriter().unlockAchievement(this.client.player, statbase, k);
        }

        this.hasStatistics = true;

        if (this.client.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter)this.client.currentScreen).onStatsUpdated();
        }
    }

    public void handleRecipeBook(SPacketRecipeBook packetIn)
    {
        RecipeBook recipebook;
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        recipebook = this.client.player.getRecipeBook();
        recipebook.setGuiOpen(packetIn.isGuiOpen());
        recipebook.setFilteringCraftable(packetIn.isFilteringCraftable());
        SPacketRecipeBook.State spacketrecipebook$state = packetIn.getState();
        label21:

        switch (spacketrecipebook$state)
        {
            case REMOVE:
                Iterator iterator = packetIn.getRecipes().iterator();

                while (true)
                {
                    if (!iterator.hasNext())
                    {
                        break label21;
                    }

                    IRecipe irecipe = (IRecipe)iterator.next();
                    recipebook.lock(irecipe);
                }

            case INIT:
                packetIn.getRecipes().forEach(recipebook::unlock);
                packetIn.getDisplayedRecipes().forEach(recipebook::markNew);
                break;

            case ADD:
                packetIn.getRecipes().forEach((p_194025_2_) ->
                {
                    recipebook.unlock(p_194025_2_);
                    recipebook.markNew(p_194025_2_);
                    RecipeToast.addOrUpdate(this.client.getToastGui(), p_194025_2_);
                });
        }

        RecipeBookClient.ALL_RECIPES.forEach((p_194023_1_) ->
        {
            p_194023_1_.updateKnownRecipes(recipebook);
        });

        if (this.client.currentScreen instanceof IRecipeShownListener)
        {
            ((IRecipeShownListener)this.client.currentScreen).recipesUpdated();
        }
    }

    public void handleEntityEffect(SPacketEntityEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity instanceof EntityLivingBase)
        {
            Potion potion = Potion.getPotionById(packetIn.getEffectId());

            if (potion != null)
            {
                PotionEffect potioneffect = new PotionEffect(potion, packetIn.getDuration(), packetIn.getAmplifier(), packetIn.getIsAmbient(), packetIn.doesShowParticles());
                potioneffect.setPotionDurationMax(packetIn.isMaxDuration());
                ((EntityLivingBase)entity).addPotionEffect(potioneffect);
            }
        }
    }

    public void handleCombatEvent(SPacketCombatEvent packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.eventType == SPacketCombatEvent.Event.ENTITY_DIED)
        {
            Entity entity = this.world.getEntityByID(packetIn.playerId);

            if (entity == this.client.player)
            {
                this.client.displayGuiScreen(new GuiGameOver(packetIn.deathMessage));
            }
        }
    }

    public void handleServerDifficulty(SPacketServerDifficulty packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.world.getWorldInfo().setDifficulty(packetIn.getDifficulty());
        this.client.world.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
    }

    public void handleCamera(SPacketCamera packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            this.client.setRenderViewEntity(entity);
        }
    }

    public void handleWorldBorder(SPacketWorldBorder packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        packetIn.apply(this.world.getWorldBorder());
    }

    @SuppressWarnings("incomplete-switch")
    public void handleTitle(SPacketTitle packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        SPacketTitle.Type spackettitle$type = packetIn.getType();
        String s = null;
        String s1 = null;
        String s2 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";

        switch (spackettitle$type)
        {
            case TITLE:
                s = s2;
                break;

            case SUBTITLE:
                s1 = s2;
                break;

            case ACTIONBAR:
                this.client.ingameGUI.setOverlayMessage(s2, false);
                return;

            case RESET:
                this.client.ingameGUI.displayTitle("", "", -1, -1, -1);
                this.client.ingameGUI.setDefaultTitlesTimes();
                return;
        }

        this.client.ingameGUI.displayTitle(s, s1, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
    }

    public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter packetIn)
    {
        this.client.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().isEmpty() ? null : packetIn.getHeader());
        this.client.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().isEmpty() ? null : packetIn.getFooter());
    }

    public void handleRemoveEntityEffect(SPacketRemoveEntityEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = packetIn.getEntity(this.world);

        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase)entity).removeActivePotionEffect(packetIn.getPotion());
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void handlePlayerListItem(SPacketPlayerListItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        for (SPacketPlayerListItem.AddPlayerData spacketplayerlistitem$addplayerdata : packetIn.getEntries())
        {
            if (packetIn.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER)
            {
                this.playerInfoMap.remove(spacketplayerlistitem$addplayerdata.getProfile().getId());
            }
            else
            {
                NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(spacketplayerlistitem$addplayerdata.getProfile().getId());

                if (packetIn.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER)
                {
                    networkplayerinfo = new NetworkPlayerInfo(spacketplayerlistitem$addplayerdata);
                    this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
                }

                if (networkplayerinfo != null)
                {
                    switch (packetIn.getAction())
                    {
                        case ADD_PLAYER:
                            networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                            networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;

                        case UPDATE_GAME_MODE:
                            networkplayerinfo.setGameType(spacketplayerlistitem$addplayerdata.getGameMode());
                            break;

                        case UPDATE_LATENCY:
                            networkplayerinfo.setResponseTime(spacketplayerlistitem$addplayerdata.getPing());
                            break;

                        case UPDATE_DISPLAY_NAME:
                            networkplayerinfo.setDisplayName(spacketplayerlistitem$addplayerdata.getDisplayName());
                    }
                }
            }
        }
    }

    public void handleKeepAlive(SPacketKeepAlive packetIn)
    {
        this.sendPacket(new CPacketKeepAlive(packetIn.getId()));
    }

    public void handlePlayerAbilities(SPacketPlayerAbilities packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        EntityPlayer entityplayer1 = this.client.player;
        entityplayer1.capabilities.isFlying = packetIn.isFlying();
        entityplayer1.capabilities.isCreativeMode = packetIn.isCreativeMode();
        entityplayer1.capabilities.disableDamage = packetIn.isInvulnerable();
        entityplayer1.capabilities.allowFlying = packetIn.isAllowFlying();
        entityplayer1.capabilities.setFlySpeed(packetIn.getFlySpeed());
        entityplayer1.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
    }

    /**
     * Displays the available command-completion options the server knows of
     */
    public void handleTabComplete(SPacketTabComplete packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        String[] astring = packetIn.getMatches();
        Arrays.sort((Object[])astring);

        if (this.client.currentScreen instanceof ITabCompleter)
        {
            ((ITabCompleter)this.client.currentScreen).setCompletions(astring);
        }
    }

    public void handleSoundEffect(SPacketSoundEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.world.playSound(this.client.player, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSound(), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch());
    }

    public void handleCustomSound(SPacketCustomSound packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(packetIn.getSoundName()), packetIn.getCategory(), packetIn.getVolume(), packetIn.getPitch(), false, 0, ISound.AttenuationType.LINEAR, (float)packetIn.getX(), (float)packetIn.getY(), (float)packetIn.getZ()));
    }

    public void handleResourcePack(SPacketResourcePackSend packetIn)
    {
        final String s = packetIn.getURL();
        final String s1 = packetIn.getHash();

        if (this.validateResourcePackUrl(s))
        {
            if (s.startsWith("level://"))
            {
                try
                {
                    String s2 = URLDecoder.decode(s.substring("level://".length()), StandardCharsets.UTF_8.toString());
                    File file1 = new File(this.client.mcDataDir, "saves");
                    File file2 = new File(file1, s2);

                    if (file2.isFile())
                    {
                        this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                        Futures.addCallback(this.client.getResourcePackRepository().setServerResourcePack(file2), this.createDownloadCallback());
                        return;
                    }
                }
                catch (UnsupportedEncodingException var7)
                {
                    ;
                }

                this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
            else
            {
                ServerData serverdata = this.client.getCurrentServerData();

                if (serverdata != null && serverdata.getResourceMode() == ServerData.ServerResourceMode.ENABLED)
                {
                    this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                    Futures.addCallback(this.client.getResourcePackRepository().downloadResourcePack(s, s1), this.createDownloadCallback());
                }
                else if (serverdata != null && serverdata.getResourceMode() != ServerData.ServerResourceMode.PROMPT)
                {
                    this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                }
                else
                {
                    this.client.addScheduledTask(new Runnable()
                    {
                        public void run()
                        {
                            NetHandlerPlayClient.this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                            {
                                public void confirmClicked(boolean result, int id)
                                {
                                    NetHandlerPlayClient.this.client = Minecraft.getMinecraft();
                                    ServerData serverdata1 = NetHandlerPlayClient.this.client.getCurrentServerData();

                                    if (result)
                                    {
                                        if (serverdata1 != null)
                                        {
                                            serverdata1.setResourceMode(ServerData.ServerResourceMode.ENABLED);
                                        }

                                        NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                                        Futures.addCallback(NetHandlerPlayClient.this.client.getResourcePackRepository().downloadResourcePack(s, s1), NetHandlerPlayClient.this.createDownloadCallback());
                                    }
                                    else
                                    {
                                        if (serverdata1 != null)
                                        {
                                            serverdata1.setResourceMode(ServerData.ServerResourceMode.DISABLED);
                                        }

                                        NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                                    }

                                    ServerList.saveSingleServer(serverdata1);
                                    NetHandlerPlayClient.this.client.displayGuiScreen((GuiScreen)null);
                                }
                            }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0));
                        }
                    });
                }
            }
        }
    }

    private boolean validateResourcePackUrl(String url)
    {
        try
        {
            URI uri = new URI(url);
            String s = uri.getScheme();
            boolean flag = "level".equals(s);

            if (!"http".equals(s) && !"https".equals(s) && !flag)
            {
                throw new URISyntaxException(url, "Wrong protocol");
            }
            else if (!flag || !url.contains("..") && url.endsWith("/resources.zip"))
            {
                return true;
            }
            else
            {
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException var5)
        {
            this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return false;
        }
    }

    private FutureCallback<Object> createDownloadCallback()
    {
        return new FutureCallback<Object>()
        {
            public void onSuccess(@Nullable Object p_onSuccess_1_)
            {
                NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }
            public void onFailure(Throwable p_onFailure_1_)
            {
                NetHandlerPlayClient.this.netManager.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
        };
    }

    public void handleUpdateBossInfo(SPacketUpdateBossInfo packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        this.client.ingameGUI.getBossOverlay().read(packetIn);
    }

    public void handleCooldown(SPacketCooldown packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.getTicks() == 0)
        {
            this.client.player.getCooldownTracker().removeCooldown(packetIn.getItem());
        }
        else
        {
            this.client.player.getCooldownTracker().setCooldown(packetIn.getItem(), packetIn.getTicks());
        }
    }

    public void handleMoveVehicle(SPacketMoveVehicle packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.client.player.getLowestRidingEntity();

        if (entity != this.client.player && entity.canPassengerSteer())
        {
            entity.setPositionAndRotation(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getYaw(), packetIn.getPitch());
            this.netManager.sendPacket(new CPacketVehicleMove(entity));
        }
    }

    /**
     * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    public void handleCustomPayload(SPacketCustomPayload packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if ("MC|TrList".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer = packetIn.getBufferData();

            try
            {
                int k = packetbuffer.readInt();
                GuiScreen guiscreen = this.client.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant && k == this.client.player.openContainer.windowId)
                {
                    IMerchant imerchant = ((GuiMerchant)guiscreen).getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
                    imerchant.setRecipes(merchantrecipelist);
                }
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Couldn't load trade info", (Throwable)ioexception);
            }
            finally
            {
                packetbuffer.release();
            }
        }
        else if ("MC|Brand".equals(packetIn.getChannelName()))
        {
            this.client.player.setServerBrand(packetIn.getBufferData().readString(32767));
        }
        else if ("MC|BOpen".equals(packetIn.getChannelName()))
        {
            EnumHand enumhand = (EnumHand)packetIn.getBufferData().readEnumValue(EnumHand.class);
            ItemStack itemstack = enumhand == EnumHand.OFF_HAND ? this.client.player.getHeldItemOffhand() : this.client.player.getHeldItemMainhand();

            if (itemstack.getItem() == Items.WRITTEN_BOOK)
            {
                this.client.displayGuiScreen(new GuiScreenBook(this.client.player, itemstack, false));
            }
        }
        else if ("MC|DebugPath".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer1 = packetIn.getBufferData();
            int l = packetbuffer1.readInt();
            float f1 = packetbuffer1.readFloat();
            Path path = Path.read(packetbuffer1);
            ((DebugRendererPathfinding)this.client.debugRenderer.pathfinding).addPath(l, path, f1);
        }
        else if ("MC|DebugNeighborsUpdate".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer2 = packetIn.getBufferData();
            long i1 = packetbuffer2.readVarLong();
            BlockPos blockpos = packetbuffer2.readBlockPos();
            ((DebugRendererNeighborsUpdate)this.client.debugRenderer.neighborsUpdate).addUpdate(i1, blockpos);
        }
        else if ("MC|StopSound".equals(packetIn.getChannelName()))
        {
            PacketBuffer packetbuffer3 = packetIn.getBufferData();
            String s = packetbuffer3.readString(32767);
            String s1 = packetbuffer3.readString(256);
            this.client.getSoundHandler().stop(s1, SoundCategory.getByName(s));
        }
    }

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    public void handleScoreboardObjective(SPacketScoreboardObjective packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();

        if (packetIn.getAction() == 0)
        {
            ScoreObjective scoreobjective = scoreboard.addScoreObjective(packetIn.getObjectiveName(), IScoreCriteria.DUMMY);
            scoreobjective.setDisplayName(packetIn.getObjectiveValue());
            scoreobjective.setRenderType(packetIn.getRenderType());
        }
        else
        {
            ScoreObjective scoreobjective1 = scoreboard.getObjective(packetIn.getObjectiveName());

            if (packetIn.getAction() == 1)
            {
                scoreboard.removeObjective(scoreobjective1);
            }
            else if (packetIn.getAction() == 2)
            {
                scoreobjective1.setDisplayName(packetIn.getObjectiveValue());
                scoreobjective1.setRenderType(packetIn.getRenderType());
            }
        }
    }

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    public void handleUpdateScore(SPacketUpdateScore packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.getObjectiveName());

        if (packetIn.getScoreAction() == SPacketUpdateScore.Action.CHANGE)
        {
            Score score = scoreboard.getOrCreateScore(packetIn.getPlayerName(), scoreobjective);
            score.setScorePoints(packetIn.getScoreValue());
        }
        else if (packetIn.getScoreAction() == SPacketUpdateScore.Action.REMOVE)
        {
            if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName()))
            {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
            }
            else if (scoreobjective != null)
            {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), scoreobjective);
            }
        }
    }

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    public void handleDisplayObjective(SPacketDisplayObjective packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();

        if (packetIn.getName().isEmpty())
        {
            scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), (ScoreObjective)null);
        }
        else
        {
            ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.getName());
            scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), scoreobjective);
        }
    }

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    public void handleTeams(SPacketTeams packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Scoreboard scoreboard = this.world.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (packetIn.getAction() == 0)
        {
            scoreplayerteam = scoreboard.createTeam(packetIn.getName());
        }
        else
        {
            scoreplayerteam = scoreboard.getTeam(packetIn.getName());
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 2)
        {
            scoreplayerteam.setDisplayName(packetIn.getDisplayName());
            scoreplayerteam.setPrefix(packetIn.getPrefix());
            scoreplayerteam.setSuffix(packetIn.getSuffix());
            scoreplayerteam.setColor(TextFormatting.fromColorIndex(packetIn.getColor()));
            scoreplayerteam.setFriendlyFlags(packetIn.getFriendlyFlags());
            Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName(packetIn.getNameTagVisibility());

            if (team$enumvisible != null)
            {
                scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }

            Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(packetIn.getCollisionRule());

            if (team$collisionrule != null)
            {
                scoreplayerteam.setCollisionRule(team$collisionrule);
            }
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 3)
        {
            for (String s : packetIn.getPlayers())
            {
                scoreboard.addPlayerToTeam(s, packetIn.getName());
            }
        }

        if (packetIn.getAction() == 4)
        {
            for (String s1 : packetIn.getPlayers())
            {
                scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
            }
        }

        if (packetIn.getAction() == 1)
        {
            scoreboard.removeTeam(scoreplayerteam);
        }
    }

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    public void handleParticles(SPacketParticles packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);

        if (packetIn.getParticleCount() == 0)
        {
            double d0 = (double)(packetIn.getParticleSpeed() * packetIn.getXOffset());
            double d2 = (double)(packetIn.getParticleSpeed() * packetIn.getYOffset());
            double d4 = (double)(packetIn.getParticleSpeed() * packetIn.getZOffset());

            try
            {
                this.world.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), d0, d2, d4, packetIn.getParticleArgs());
            }
            catch (Throwable var17)
            {
                LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticleType());
            }
        }
        else
        {
            for (int k = 0; k < packetIn.getParticleCount(); ++k)
            {
                double d1 = this.avRandomizer.nextGaussian() * (double)packetIn.getXOffset();
                double d3 = this.avRandomizer.nextGaussian() * (double)packetIn.getYOffset();
                double d5 = this.avRandomizer.nextGaussian() * (double)packetIn.getZOffset();
                double d6 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d7 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double d8 = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();

                try
                {
                    this.world.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate() + d1, packetIn.getYCoordinate() + d3, packetIn.getZCoordinate() + d5, d6, d7, d8, packetIn.getParticleArgs());
                }
                catch (Throwable var16)
                {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)packetIn.getParticleType());
                    return;
                }
            }
        }
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    public void handleEntityProperties(SPacketEntityProperties packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.client);
        Entity entity = this.world.getEntityByID(packetIn.getEntityId());

        if (entity != null)
        {
            if (!(entity instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            }
            else
            {
                AbstractAttributeMap abstractattributemap = ((EntityLivingBase)entity).getAttributeMap();

                for (SPacketEntityProperties.Snapshot spacketentityproperties$snapshot : packetIn.getSnapshots())
                {
                    IAttributeInstance iattributeinstance = abstractattributemap.getAttributeInstanceByName(spacketentityproperties$snapshot.getName());

                    if (iattributeinstance == null)
                    {
                        iattributeinstance = abstractattributemap.registerAttribute(new RangedAttribute((IAttribute)null, spacketentityproperties$snapshot.getName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    iattributeinstance.setBaseValue(spacketentityproperties$snapshot.getBaseValue());
                    iattributeinstance.removeAllModifiers();

                    for (AttributeModifier attributemodifier : spacketentityproperties$snapshot.getModifiers())
                    {
                        iattributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    public void func_194307_a(SPacketPlaceGhostRecipe p_194307_1_)
    {
        PacketThreadUtil.checkThreadAndEnqueue(p_194307_1_, this, this.client);
        Container container = this.client.player.openContainer;

        if (container.windowId == p_194307_1_.func_194313_b() && container.getCanCraft(this.client.player))
        {
            if (this.client.currentScreen instanceof IRecipeShownListener)
            {
                GuiRecipeBook guirecipebook = ((IRecipeShownListener)this.client.currentScreen).func_194310_f();
                guirecipebook.setupGhostRecipe(p_194307_1_.func_194311_a(), container.inventorySlots);
            }
        }
    }

    /**
     * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
     */
    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap()
    {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(UUID uniqueId)
    {
        return this.playerInfoMap.get(uniqueId);
    }

    @Nullable

    /**
     * Gets the client's description information about another player on the server.
     */
    public NetworkPlayerInfo getPlayerInfo(String name)
    {
        for (NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values())
        {
            if (networkplayerinfo.getGameProfile().getName().equals(name))
            {
                return networkplayerinfo;
            }
        }

        return null;
    }

    public GameProfile getGameProfile()
    {
        return this.profile;
    }

    public ClientAdvancementManager getAdvancementManager()
    {
        return this.advancementManager;
    }
}
