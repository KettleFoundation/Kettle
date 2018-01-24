package net.minecraft.util.text;

public class TextComponentSelector extends TextComponentBase
{
    /**
     * The selector used to find the matching entities of this text component
     */
    private final String selector;

    public TextComponentSelector(String selectorIn)
    {
        this.selector = selectorIn;
    }

    /**
     * Gets the selector of this component, in plain text.
     */
    public String getSelector()
    {
        return this.selector;
    }

    /**
     * Gets the raw content of this component (but not its sibling components), without any formatting codes. For
     * example, this is the raw text in a {@link TextComponentString}, but it's the translated text for a {@link
     * TextComponentTranslation} and it's the score value for a {@link TextComponentScore}.
     */
    public String getUnformattedComponentText()
    {
        return this.selector;
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public TextComponentSelector createCopy()
    {
        TextComponentSelector textcomponentselector = new TextComponentSelector(this.selector);
        textcomponentselector.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent itextcomponent : this.getSiblings())
        {
            textcomponentselector.appendSibling(itextcomponent.createCopy());
        }

        return textcomponentselector;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof TextComponentSelector))
        {
            return false;
        }
        else
        {
            TextComponentSelector textcomponentselector = (TextComponentSelector)p_equals_1_;
            return this.selector.equals(textcomponentselector.selector) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}
