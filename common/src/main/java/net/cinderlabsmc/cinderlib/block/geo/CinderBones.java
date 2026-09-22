package net.cinderlabsmc.cinderlib.block.geo;

import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Per-instance bone adjustments collected on the client in {@link CinderGeoBlockEntity#adjustBones(CinderBones)}
 * and applied by the renderer. Plain data, no GeckoLib types.
 */
public final class CinderBones {

    private final Map<String, Bone> bones = new LinkedHashMap<>();

    public @NonNull CinderBones hide(@NonNull String bone) {
        return visible(bone, false);
    }

    public @NonNull CinderBones show(@NonNull String bone) {
        return visible(bone, true);
    }

    public @NonNull CinderBones visible(@NonNull String bone, boolean visible) {
        bone(bone).hidden = !visible;
        return this;
    }

    /** Multiplies the bone's scale. */
    public @NonNull CinderBones scale(@NonNull String bone, float x, float y, float z) {
        var entry = bone(bone);
        entry.scaleX *= x;
        entry.scaleY *= y;
        entry.scaleZ *= z;
        return this;
    }

    public boolean isEmpty() {
        return bones.isEmpty();
    }

    public void forEach(@NonNull BiConsumer<String, Bone> consumer) {
        bones.forEach(consumer);
    }

    private Bone bone(String name) {
        return bones.computeIfAbsent(name, n -> new Bone());
    }

    public static final class Bone {
        private boolean hidden;
        private float scaleX = 1;
        private float scaleY = 1;
        private float scaleZ = 1;

        public boolean hidden() {
            return hidden;
        }

        public boolean scaled() {
            return scaleX != 1 || scaleY != 1 || scaleZ != 1;
        }

        public float scaleX() {
            return scaleX;
        }

        public float scaleY() {
            return scaleY;
        }

        public float scaleZ() {
            return scaleZ;
        }
    }
}
