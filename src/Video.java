import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;

import java.util.Arrays;

public class EdgeDetection extends PApplet
{
    private Capture video;
    public int threshold;

    double[][] edge_det = { { 0,  1, 0 },{ 1, -4, 1 }, { 0,  1, 0 } };

    public static void main(String[] args)
    {
        PApplet.main("Video");
    }

    public void settings()
    {
        size(640, 480);
    }

    public void setup()
    {
        threshold = 250;
       

        String[] cameras = new String[0];
        while(cameras.length == 0) {
            cameras = Capture.list();
            System.out.println("Here: " + Arrays.toString(cameras));
        }
        video = new Capture(this, cameras[0]);
        video.start();
    }

    public void draw() {
        if(video.available())
            video.read();

        image(convolution(frame, kernel, 3), 0 , 0);

    }

    public PImage convolution(PImage input, double[][] kernel, int kernelSize) {

        PImage frame = createImage(input.width, input.height, RGB);
        frame.loadPixels();
        for (int x = 1; x < input.width-1; x++) {
            for (int y = 1; y < input.height-1; y++ ) {
                frame.pixels[x + y*img.width] = applyConvolution(x, y, kernel, kernelsize, input);
            }
        }
        frame.updatePixels();
        return frame;
    }

    public PImage grayScale(Pimage input) {
        input.loadPixels();
        for (int x = 1; x < input.width-1; x++) {
            for (int y = 1; y < input.height-1; y++ ) {
                int loc = x + y*img.width;
                int average = (red(img.pixels[loc]) + green(img.pixels[loc]) + blue(img.pixels[loc])) / 3;
                input.pixels[loc] = color(average, average, average);
            }
        }
        input.updatePixels();
        return input;
    }

    private color applyKernel(PImage img, int x, int y, double[][] matrix, int matrixsize)
    {
        float rtotal = 0.0;
        float gtotal = 0.0;
        float btotal = 0.0;
        int offset = matrixsize / 2;
        for (int i = 0; i < matrixsize; i++){
            for (int j= 0; j < matrixsize; j++){
            int xloc = x+i-offset;
            int yloc = y+j-offset;
            int loc = xloc + img.width*yloc;
            loc = constrain(loc,0,img.pixels.length-1);
            rtotal += (red(img.pixels[loc]) * matrix[i][j]);
            gtotal += (green(img.pixels[loc]) * matrix[i][j]);
            btotal += (blue(img.pixels[loc]) * matrix[i][j]);
            }
        }
        // Make sure RGB is within range
        rtotal = constrain(rtotal, 0, 255);
        gtotal = constrain(gtotal, 0, 255);
        btotal = constrain(btotal, 0, 255);
        // Return the resulting color
        return color(rtotal, gtotal, btotal);
    }

    
    public PImage sobel(PImage input, double[][] kernel)
    {
        if(kernel[0].length != kernel.length)
            throw Exception("Bad");

        PImage img = createImage(input.width, input.height, RGB);
        
        input.filter(GRAY);
        input.loadPixels();
        
        img.loadPixels();

        for (int x = 1; x < input.width - 1; x++)
        {
            for(int y = 1; y < input.height - 1; y++)
            {
                double value = red(applyKernel(input, x, y, kernel, kernel.length));
                
                if(value > threshold)
                    img.pixels[x + y * img.width] = color(255, 255, 255);
                else
                    img.pixels[x + y * img.width] = color(0, 0, 0);
            }
        }
    }
}