package mc.kettle.kettleserver.internal.remap.mappers;

public class MethodRedirectRule {
    private final String owner, description, name, remapOwner;

    public MethodRedirectRule(String owner, String description, String name, String remapOwner) {
        this.owner = owner;
        this.description = description;
        this.name = name;
        this.remapOwner = remapOwner;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getRemapOwner() {
        return remapOwner;
    }
}