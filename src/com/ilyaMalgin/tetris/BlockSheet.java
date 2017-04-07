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

    public static final BufferedImage I_SHAPE_IMAGE = blocksSheet.getSubimage(0, 0, 40, 40);
    public static final BufferedImage CUBE_SHAPE_IMAGE = blocksSheet.getSubimage(40, 0, 40, 40);
    public static final BufferedImage T_SHAPE_IMAGE = blocksSheet.getSubimage(80, 0, 40, 40);
    public static final BufferedImage Z_SHAPE_IMAGE = blocksSheet.getSubimage(120, 0, 40, 40);
    public static final BufferedImage L_SHAPE_IMAGE = blocksSheet.getSubimage(0, 40, 40, 40);

}
