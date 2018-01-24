package net.minecraft.util.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;

public interface ITextComponent extends Iterable<ITextComponent>
{
    /**
     * Sets the style of this component and updates the parent style of all of the sibling components.
     */
    ITextComponent setStyle(Style style);

    /**
     * Gets the style of this component. Returns a direct reference; changes to this style will modify the style of this
     * component (IE, there is no need to call {@link #setStyle(Style)} again after modifying it).
     *  
     * If this component's style is currently <code>null</code>, it will be initialized to the default style, and the
     * parent style of all sibling components will be set to that style. (IE, changes to this style will also be
     * reflected in sibling components.)
     *  
     * This method never returns <code>null</code>.
     */
    Style getStyle();

    /**
     * Adds a new component to the end of the sibling list, with the specified text. Same as calling {@link
     * #appendSibling(ITextComponent)} with a new {@link TextComponentString}.
     *  
     * @return This component, for chaining (and not the newly added component)
     */
    ITextComponent appendText(String text);

    /**
     * Adds a new component to the end of the sibling list, setting that component's style's parent style to this
     * component's style.
     *  
     * @return This component, for chaining (and not the newly added component)
     */
    ITextComponent appendSibling(ITextComponent component);

    /**
     * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
     * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
     * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
     */
    String getUnformattedComponentText();

    /**
     * Gets the text of this component <em>and all sibling components</em>, without any formatting codes.
     */
    String getUnformattedText();

    /**
     * Gets the text of this component <em>and all sibling components</em>, with formatting codes added for rendering.
     */
    String getFormattedText();

    List<ITextComponent> getSiblings();

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    ITextComponent createCopy();

    public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent>
    {
        private static final Gson GSON;

        public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonPrimitive())
            {
                return new TextComponentString(p_deserialize_1_.getAsString());
            }
            else if (!p_deserialize_1_.isJsonObject())
            {
                if (p_deserialize_1_.isJsonArray())
                {
                    JsonArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
                    ITextComponent itextcomponent1 = null;

                    for (JsonElement jsonelement : jsonarray1)
                    {
                        ITextComponent itextcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);

                        if (itextcomponent1 == null)
                        {
                            itextcomponent1 = itextcomponent2;
                        }
                        else
                        {
                            itextcomponent1.appendSibling(itextcomponent2);
                        }
                    }

                    return itextcomponent1;
                }
                else
                {
                    throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                }
            }
            else
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                ITextComponent itextcomponent;

                if (jsonobject.has("text"))
                {
                    itextcomponent = new TextComponentString(jsonobject.get("text").getAsString());
                }
                else if (jsonobject.has("translate"))
                {
                    String s = jsonobject.get("translate").getAsString();

                    if (jsonobject.has("with"))
                    {
                        JsonArray jsonarray = jsonobject.getAsJsonArray("with");
                        Object[] aobject = new Object[jsonarray.size()];

                        for (int i = 0; i < aobject.length; ++i)
                        {
                            aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);

                            if (aobject[i] instanceof TextComponentString)
                            {
                                TextComponentString textcomponentstring = (TextComponentString)aobject[i];

                                if (textcomponentstring.getStyle().isEmpty() && textcomponentstring.getSiblings().isEmpty())
                                {
                                    aobject[i] = textcomponentstring.getText();
                                }
                            }
                        }

                        itextcomponent = new TextComponentTranslation(s, aobject);
                    }
                    else
                    {
                        itextcomponent = new TextComponentTranslation(s, new Object[0]);
                    }
                }
                else if (jsonobject.has("score"))
                {
                    JsonObject jsonobject1 = jsonobject.getAsJsonObject("score");

                    if (!jsonobject1.has("name") || !jsonobject1.has("objective"))
                    {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }

                    itextcomponent = new TextComponentScore(JsonUtils.getString(jsonobject1, "name"), JsonUtils.getString(jsonobject1, "objective"));

                    if (jsonobject1.has("value"))
                    {
                        ((TextComponentScore)itextcomponent).setValue(JsonUtils.getString(jsonobject1, "value"));
                    }
                }
                else if (jsonobject.has("selector"))
                {
                    itextcomponent = new TextComponentSelector(JsonUtils.getString(jsonobject, "selector"));
                }
                else
                {
                    if (!jsonobject.has("keybind"))
                    {
                        throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
                    }

                    itextcomponent = new TextComponentKeybind(JsonUtils.getString(jsonobject, "keybind"));
                }

                if (jsonobject.has("extra"))
                {
                    JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");

                    if (jsonarray2.size() <= 0)
                    {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int j = 0; j < jsonarray2.size(); ++j)
                    {
                        itextcomponent.appendSibling(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
                    }
                }

                itextcomponent.setStyle((Style)p_deserialize_3_.deserialize(p_deserialize_1_, Style.class));
                return itextcomponent;
            }
        }

        private void serializeChatStyle(Style style, JsonObject object, JsonSerializationContext ctx)
        {
            JsonElement jsonelement = ctx.serialize(style);

            if (jsonelement.isJsonObject())
            {
                JsonObject jsonobject = (JsonObject)jsonelement;

                for (Entry<String, JsonElement> entry : jsonobject.entrySet())
                {
                    object.add(entry.getKey(), entry.getValue());
                }
            }
        }

        public JsonElement serialize(ITextComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject jsonobject = new JsonObject();

            if (!p_serialize_1_.getStyle().isEmpty())
            {
                this.serializeChatStyle(p_serialize_1_.getStyle(), jsonobject, p_serialize_3_);
            }

            if (!p_serialize_1_.getSiblings().isEmpty())
            {
                JsonArray jsonarray = new JsonArray();

                for (ITextComponent itextcomponent : p_serialize_1_.getSiblings())
                {
                    jsonarray.add(this.serialize(itextcomponent, itextcomponent.getClass(), p_serialize_3_));
                }

                jsonobject.add("extra", jsonarray);
            }

            if (p_serialize_1_ instanceof TextComponentString)
            {
                jsonobject.addProperty("text", ((TextComponentString)p_serialize_1_).getText());
            }
            else if (p_serialize_1_ instanceof TextComponentTranslation)
            {
                TextComponentTranslation textcomponenttranslation = (TextComponentTranslation)p_serialize_1_;
                jsonobject.addProperty("translate", textcomponenttranslation.getKey());

                if (textcomponenttranslation.getFormatArgs() != null && textcomponenttranslation.getFormatArgs().length > 0)
                {
                    JsonArray jsonarray1 = new JsonArray();

                    for (Object object : textcomponenttranslation.getFormatArgs())
                    {
                        if (object instanceof ITextComponent)
                        {
                            jsonarray1.add(this.serialize((ITextComponent)object, object.getClass(), p_serialize_3_));
                        }
                        else
                        {
                            jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                        }
                    }

                    jsonobject.add("with", jsonarray1);
                }
            }
            else if (p_serialize_1_ instanceof TextComponentScore)
            {
                TextComponentScore textcomponentscore = (TextComponentScore)p_serialize_1_;
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.addProperty("name", textcomponentscore.getName());
                jsonobject1.addProperty("objective", textcomponentscore.getObjective());
                jsonobject1.addProperty("value", textcomponentscore.getUnformattedComponentText());
                jsonobject.add("score", jsonobject1);
            }
            else if (p_serialize_1_ instanceof TextComponentSelector)
            {
                TextComponentSelector textcomponentselector = (TextComponentSelector)p_serialize_1_;
                jsonobject.addProperty("selector", textcomponentselector.getSelector());
            }
            else
            {
                if (!(p_serialize_1_ instanceof TextComponentKeybind))
                {
                    throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_ + " as a Component");
                }

                TextComponentKeybind textcomponentkeybind = (TextComponentKeybind)p_serialize_1_;
                jsonobject.addProperty("keybind", textcomponentkeybind.getKeybind());
            }

            return jsonobject;
        }

        public static String componentToJson(ITextComponent component)
        {
            return GSON.toJson(component);
        }

        @Nullable
        public static ITextComponent jsonToComponent(String json)
        {
            return (ITextComponent)JsonUtils.gsonDeserialize(GSON, json, ITextComponent.class, false);
        }

        @Nullable
        public static ITextComponent fromJsonLenient(String json)
        {
            return (ITextComponent)JsonUtils.gsonDeserialize(GSON, json, ITextComponent.class, true);
        }

        static
        {
            GsonBuilder gsonbuilder = new GsonBuilder();
            gsonbuilder.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
            gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
            gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
            GSON = gsonbuilder.create();
        }
    }
}
