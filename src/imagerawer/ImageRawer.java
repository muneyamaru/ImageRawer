/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagerawer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class ImageRawer {
    public static byte[] rawify(BufferedImage input) {
        // channels
        int l = input.getHeight() * input.getWidth();
        byte[] red = new byte[l], green = new byte[l], blue = new byte[l];
        
        for(int y=0;y<input.getHeight();y++) {
            for(int x=0;x<input.getWidth();x++) {
                int rgb = input.getRGB(x,y);
                byte r = (byte)((rgb&0xff0000)>>16);
                byte g = (byte)((rgb&0x00ff00)>> 8);
                byte b = (byte)((rgb&0x0000ff)    );
                
                red  [y*input.getHeight()+x]=r;
                green[y*input.getHeight()+x]=g;
                blue [y*input.getHeight()+x]=b;
            }
        }
        byte[] data = new byte[l*3];
        System.arraycopy(red  ,0,data,  0,l);
        System.arraycopy(green,0,data,  l,l);
        System.arraycopy(blue ,0,data,2*l,l);
        
        return data;
    }
    
    public static BufferedImage derawify(byte[] raw, int w) {
        if(raw.length%3!=0) throw new IllegalArgumentException("Bad raw data.");
        int l = raw.length/3;
        if(l%w!=0) throw new IllegalArgumentException("Bad width.");
        byte[] red = new byte[l], green = new byte[l], blue = new byte[l];
        
        System.arraycopy(raw,  0,red  ,0,l);
        System.arraycopy(raw,  l,green,0,l);
        System.arraycopy(raw,2*l,blue ,0,l);
        
        BufferedImage bi = new BufferedImage(w,l/w,BufferedImage.TYPE_INT_RGB);
        
        for(int y=0;y<bi.getHeight();y++) {
            for(int x=0;x<bi.getWidth();x++) {
                int col = (red[y*bi.getHeight()+x]<<16) + (green[y*bi.getHeight()+x]<<8) + blue[y*bi.getHeight()+x]; 
                bi.setRGB(x,y,col);
            }
        }
        
        for(int i=0;i<bi.getWidth()*bi.getHeight();i++) {
            int col=(red[i]<<16)+(green[i]<<8)+blue[i];
            bi.setRGB(i%w,i/w,col);
        }
        return bi;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        String d;
        System.out.print("Enter path to image/data: ");
        d = in.nextLine();
        String path = d;
        System.out.print("Enter path to output: ");
        d = in.nextLine();
        String path2 = d;
        System.out.print("Raw or deraw? R/d: ");
        d = in.nextLine();
        if(d.equals("d")) {
            // deraw
            System.out.print("Enter the width of the image: ");
            d = in.nextLine();
            int w = Integer.parseInt(d);
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            BufferedImage bi = derawify(data,w);
            ImageIO.write(bi,"png",new File(path2));
        } else {
            // raw
            File f = new File(path);
            BufferedImage bi = ImageIO.read(f);
            byte[] out = rawify(bi);
            File f2 = new File(path2);
            FileOutputStream fos = new FileOutputStream(f2);
            fos.write(out);
            fos.close();
        }
    }
    
}
