package image_caption;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import java.io.*;

public class Image {
    private BufferedImage _img;
    private Graphics2D _img_g;
    private int _width;
    private int _height;
    private BufferedImage _bg;
    private Color _bg_color;
    private Color _fg_color;

    public Image(int width, int height) {
        _img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        _img_g = _img.createGraphics();
        _width = width;
        _height = height;
    }

    public void renderSprite(String filename, int x, int y, int flip, float scale) throws IOException {
        BufferedImage clip_img = ImageIO.read(new File(filename));

        if (flip == 1)
        {
            // Flip the image
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-clip_img.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            clip_img = op.filter(clip_img, null);
        }

        // Scale the image
        AffineTransform ts = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp op = new AffineTransformOp(ts, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        clip_img = op.filter(clip_img, null);

        x -= clip_img.getWidth(null) / 2.0;
        y -= clip_img.getHeight(null) / 2.0;

        _img_g.drawImage(clip_img, x, y, null);
    }

    public int[] histogram(int new_width, int new_height, int scalex, int scaley) {
        // scalex and scaley are scale factors for the resized image. 
        // e.g. scalex = 0.5 will give an image with half the x coordinates
        // http://stackoverflow.com/questions/15558202/how-to-resize-image-in-java

        
    	BufferedImage scaled = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = scaled.createGraphics();
    	AffineTransform transformation = AffineTransform.getScaleInstance(scalex, scaley);
    	g.drawRenderedImage(_img, transformation);

        BufferedImage gray_scaled = new BufferedImage(new_width, new_height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g2 = gray_scaled.getGraphics();
        g2.drawImage(scaled, 0, 0, null);

        int histogram[] = new int[256];
        for (int i = 0; i < 256; i++) {
            histogram[i] = 0;
        }

        for (int x = 0; x < new_width; x++) {
            for (int y = 0; y < new_height; y++) {
                int pixel = gray_scaled.getRGB(x, y) & 0xFF;
                histogram[pixel]++;
            }
        }
        return histogram;
    }

    public void save(String fileName) throws IOException {
        ImageIO.write(_img, "png", new File(fileName));
    }

    public void load(String fileName) throws IOException {
        _img = ImageIO.read(new File(fileName));
    }

    public int[][] getGrayscalePixels() {
        BufferedImage gray_img = new BufferedImage(_width, _height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = gray_img.getGraphics();
        g.drawImage(_img, 0, 0, null);

        int ret[][] = new int[_height][_width];
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                ret[y][x] = gray_img.getRGB(x, y)& 0xFF;
            }
        }
        return ret;
    }
}
