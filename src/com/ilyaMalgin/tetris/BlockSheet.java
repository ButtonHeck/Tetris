package com.ilyaMalgin.tetris;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BlockSheet {

    private static BufferedImage blocksSheet;
    static {
        try {
            blocksSheet = ImageIO.read(new File("res/img/blockSheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final BufferedImage I_1 = blocksSheet.getSubimage(0, 0, 40, 40);
    private static final BufferedImage I_2 = blocksSheet.getSubimage(40, 0, 40, 40);
    private static final BufferedImage I_3 = blocksSheet.getSubimage(80, 0, 40, 40);
    public static final BufferedImage[] I_SHAPE_IMAGES = new BufferedImage[] {I_1, I_2, I_3};

    private static final BufferedImage L_1 = blocksSheet.getSubimage(0, 40, 40, 40);
    private static final BufferedImage L_2 = blocksSheet.getSubimage(40, 40, 40, 40);
    private static final BufferedImage L_3 = blocksSheet.getSubimage(80, 40, 40, 40);
    public static final BufferedImage[] L_SHAPE_IMAGES = new BufferedImage[] {L_1, L_2, L_3};

    private static final BufferedImage CUBE_1 = blocksSheet.getSubimage(0, 80, 40, 40);
    private static final BufferedImage CUBE_2 = blocksSheet.getSubimage(40, 80, 40, 40);
    private static final BufferedImage CUBE_3 = blocksSheet.getSubimage(80, 80, 40, 40);
    public static final BufferedImage[] CUBE_SHAPE_IMAGES = new BufferedImage[] {CUBE_1, CUBE_2, CUBE_3};

    private static final BufferedImage T_1 = blocksSheet.getSubimage(0, 120, 40, 40);
    private static final BufferedImage T_2 = blocksSheet.getSubimage(40, 120, 40, 40);
    private static final BufferedImage T_3 = blocksSheet.getSubimage(80, 120, 40, 40);
    public static final BufferedImage[] T_SHAPE_IMAGES = new BufferedImage[] {T_1, T_2, T_3};

    private static final BufferedImage Z_1 = blocksSheet.getSubimage(120, 0, 40, 40);
    private static final BufferedImage Z_2 = blocksSheet.getSubimage(120, 40, 40, 40);
    private static final BufferedImage Z_3 = blocksSheet.getSubimage(120, 80, 40, 40);
    public static final BufferedImage[] Z_SHAPE_IMAGES = new BufferedImage[] {Z_1, Z_2, Z_3};
}
