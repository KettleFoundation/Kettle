package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion
{
    private final ICriterionInstance criterionInstance;

    public Criterion(ICriterionInstance p_i47470_1_)
    {
        this.criterionInstance = p_i47470_1_;
    }

    public Criterion()
    {
        this.criterionInstance = null;
    }

    public void serializeToNetwork(PacketBuffer p_192140_1_)
    {
    }

    /**
     * Deserialize a <em>single</em> {@code Criterion} from {@code json}. The {@link ICriterionTrigger} is chosen by the
     * {@code "trigger"} property of the object, which can then handle the optional {@code "conditions"} in the object.
     * The {@code "conditions"}, if present, must be a {@code JsonObject}. The resulting {@link ICriterionInstance} is
     * wrapped in a {@code Criterion}.

     * @return the deserialized {@code Criterion}.
     * @see ICriterionTrigger#deserializeInstance(JsonObject, JsonDeserializationContext)
     *  
     * @param json the {@code JsonObject} to deserialize from
     * @param context the {@code JsonDeserializationContext} to deserialize in
     */
    public static Criterion criterionFromJson(JsonObject json, JsonDeserializationContext context)
    {
        ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "trigger"));
        ICriterionTrigger<?> icriteriontrigger = CriteriaTriggers.get(resourcelocation);

        if (icriteriontrigger == null)
        {
            throw new JsonSyntaxException("Invalid criterion trigger: " + resourcelocation);
        }
        else
        {
            ICriterionInstance icriterioninstance = icriteriontrigger.deserializeInstance(JsonUtils.getJsonObject(json, "conditions", new JsonObject()), context);
            return new Criterion(icriterioninstance);
        }
    }

    public static Criterion criterionFromNetwork(PacketBuffer p_192146_0_)
    {
        return new Criterion();
    }

    public static Map<String, Criterion> criteriaFromJson(JsonObject json, JsonDeserializationContext context)
    {
        Map<String, Criterion> map = Maps.<String, Criterion>newHashMap();

        for (Entry<String, JsonElement> entry : json.entrySet())
        {
            map.put(entry.getKey(), criterionFromJson(JsonUtils.getJsonObject(entry.getValue(), "criterion"), context));
        }

        return map;
    }

    public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer bus)
    {
        Map<String, Criterion> map = Maps.<String, Criterion>newHashMap();
        int i = bus.readVarInt();

        for (int j = 0; j < i; ++j)
        {
            map.put(bus.readString(32767), criterionFromNetwork(bus));
        }

        return map;
    }

    /**
     * Write {@code criteria} to {@code buf}.

     * @see #criteriaFromNetwork(PacketBuffer)
     *  
     * @param criteria the criteria to write to {@code buf}. Each key-value pair consists of a {@code Criterion} and its
     * name.
     * @param buf the {@code PacketBuffer} to write to
     */
    public static void serializeToNetwork(Map<String, Criterion> criteria, PacketBuffer buf)
    {
        buf.writeVarInt(criteria.size());

        for (Entry<String, Criterion> entry : criteria.entrySet())
        {
            buf.writeString(entry.getKey());
            ((Criterion)entry.getValue()).serializeToNetwork(buf);
        }
    }

    @Nullable
    public ICriterionInstance getCriterionInstance()
    {
        return this.criterionInstance;
    }
}
