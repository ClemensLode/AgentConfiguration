package agentconfiguration;

/**
 *
 * @author Clemens Lode, clemens at lode.de, University Karlsruhe (TH), clemens@lode.de
 */

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;


class ImageRenderComponent extends JPanel {
    BufferedImage image;
    Dimension size;
    
    public ImageRenderComponent() {
    } 

    public ImageRenderComponent(BufferedImage image) {
        this.image = image;
        size = new Dimension(image.getWidth(), image.getHeight());
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
        size = new Dimension(image.getWidth(), image.getHeight());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }
}

