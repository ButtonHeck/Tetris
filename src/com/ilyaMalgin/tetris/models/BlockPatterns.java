package com.ilyaMalgin.tetris.models;

public abstract class BlockPatterns {
    private static Block[] get_L_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(0, -1),
                new Block(0, 1),
                new Block(1, 1)
        };
    }

    private static Block[] get_L_mirror_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(0, -1),
                new Block(0, 1),
                new Block(-1, 1)
        };
    }

    private static Block[] get_T_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(1, 0),
                new Block(-1, 0),
                new Block(0, -1)
        };
    }

    private static Block[] get_I_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(1, 0),
                new Block(2, 0),
                new Block(-1, 0)
        };
    }

    private static Block[] get_Cube_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(1, 0),
                new Block(1, 1),
                new Block(0, 1)
        };
    }

    private static Block[] get_Z_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(0, -1),
                new Block(1, 0),
                new Block(1, 1)
        };
    }

    private static Block[] get_Z_mirror_pattern() {
        return new Block[]{
                new Block(0, 0),
                new Block(0, -1),
                new Block(-1, 0),
                new Block(-1, 1)
        };
    }

    public static Block[] getBlocks(int blockPattern) {
        switch (blockPattern) {
            case 0: {
                return get_L_pattern();
            }
            case 1: {
                return get_T_pattern();
            }
            case 2: {
                return get_I_pattern();
            }
            case 3: {
                return get_Cube_pattern();
            }
            case 4: {
                return get_Z_pattern();
            }
            case 5: {
                return get_L_mirror_pattern();
            }
            case 6: {
                return get_Z_mirror_pattern();
            }
            default: {
                throw new IllegalArgumentException("Incorrect pattern index: " + blockPattern);
            }
        }
    }
}
