package shcm.shsupercm.fabric.citresewn.defaults.common;

import net.minecraft.resources.Identifier;

/**
 * Marks models as cit models.
 */
public class ResewnItemModelIdentifier {
    private static final String MARKER = "citresewn_model_path";

    public static Identifier pack(Identifier id) {
        return Identifier.fromNamespaceAndPath(MARKER, id.getNamespace() + '/' + id.getPath());
    }

    public static boolean marked(Identifier id) {
        return id.getNamespace().equals(MARKER);
    }

    public static Identifier unpack(Identifier id) {
        if (!marked(id))
            throw new IllegalArgumentException("The given Identifier is not a packed resewn model");

        int split = id.getPath().indexOf('/');
        return Identifier.fromNamespaceAndPath(id.getPath().substring(0, split), id.getPath().substring(split + 1));
    }
}
