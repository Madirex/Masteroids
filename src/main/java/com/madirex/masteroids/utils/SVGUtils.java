package com.madirex.masteroids.utils;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class SVGUtils {

    private SVGUtils(){

    }

    public static ImageView svg2image(String svgName, float scaleFactor) {
        try {
            File dir = new File(Util.SVG_FOLDER + svgName + ".svg");
            SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(dir.toURI());

            int width = (int) (diagram.getWidth() * scaleFactor);
            int height = (int) (diagram.getHeight() * scaleFactor);

            if(width < 1){
                if (Util.DEBUG) {
                    System.out.println("💠 Tamaño width de sprite escalado a 1.");
                }
                width = 1;
            }

            if(height < 1){
                if (Util.DEBUG) {
                    System.out.println("💠 Tamaño height de sprite escalado a 1.");
                }
                height = 1;
            }

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB );

            Graphics2D g = image.createGraphics();
            try {
                g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

                if( scaleFactor != 1f) g.scale(scaleFactor,scaleFactor);

                diagram.setIgnoringClipHeuristic( true );

                diagram.render( g );
            } finally {
                g.dispose();
            }

            Image img = SwingFXUtils.toFXImage(image, null);
            ImageView imageView = new ImageView();
            imageView.setImage(img);

            return imageView;

        } catch(SVGException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
