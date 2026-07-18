package dev.firecontroller.oshaa;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public final class OAUtil {

    private OAUtil() {
        // ...
    }

    /**
     * Creates four horizontal variants from a base shape
     * by rotating it by 90 degrees to match a blockstate y-rotation.
     * @param base The base voxel shape.
     * @param direction The newly created direction of the voxel.
     * @return A map between each direction and its associated voxel shape.
     */
    public static Map<Direction, VoxelShape> createHorizontalShapes(VoxelShape base, Direction direction) {
        EnumMap<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        VoxelShape currentShape = base;
        Direction currentDirection = direction;
        for (int i = 0; i < 4; i++) {
            shapes.put(currentDirection, currentShape);
            currentShape = rotateY90(currentShape);
            currentDirection = currentDirection.getClockWise();
        }
        return Map.copyOf(shapes);
    }

    /**
     * Rotates a {@link VoxelShape} 90 degrees clockwise around the Y axis.
     * @param shape The shape to rotate 90 degrees.
     * @return The rotated voxel shape.
     */
    private static VoxelShape rotateY90(VoxelShape shape) {
        VoxelShape[] result = {Shapes.empty() };
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            result[0] = Shapes.or(result[0], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX));
        });
        return result[0];
    }

}
