import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;

import java.util.Arrays;

public class EdgeDetection extends PApplet
{
    private Capture video;
    private PImage frame;
    public int threshold;

    public static void main(String[] args)
    {
        System.out.println("Start");
        PApplet.main("EdgeDetection");
    }

    public void settings()
    {
        size(640, 480);
    }

    public void setup()
    {
        threshold = 10;
        System.out.println("Running Setup");
        String[] cameras = new String[0];
        while(cameras.length == 0) {
            cameras = Capture.list();
            System.out.println("Here: " + Arrays.toString(cameras));
        }
        video = new Capture(this, cameras[0]);
        video.start();
    }

    public PImage grayScale(PImage input) {
        input.loadPixels();
        for (int x = 0; x < input.width; x++) {
            for (int y = 0; y < input.height; y++ ) {
                int loc = x + y*input.width;
                double average = (red(input.pixels[loc]) + green(input.pixels[loc]) + blue(input.pixels[loc])) / 3;
                input.pixels[loc] = color((int)average, (int)average, (int)average);
            }
        }
        input.updatePixels();
        return input;
    }

    public PImage inverse(PImage input) {
        input.loadPixels();
        for (int x = 0; x < input.width; x++) {
            for (int y = 0; y < input.height; y++ ) {
                int loc = x + y*input.width;
                input.pixels[loc] = color(255 - red(input.pixels[loc]), 255 - green(input.pixels[loc]), 255 - blue(input.pixels[loc]));
            }
        }
        input.updatePixels();
        return input;
    }

    double[][] edge_det = { { 0,  1, 0 }, { 1, -4, 1 }, { 0,  1, 0 } };

    double[][] blur = {{1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}};

    double[][] identity = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};

    double[][] sharpen = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};

    double[][] ones = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

    int[][] deltas = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {1, -1}};

    public void draw() {
        if(video.available())
            video.read();

        image(convolve(sobel(convolve(video, blur), edge_det), sharpen), 0, 0);

    }

    private PImage neighborFilter(PImage input) {

        PImage img = createImage(input.width, input.height, RGB);

        input.loadPixels();
        img.loadPixels();
        for(int i = 0; i < input.width; i++) {
            for(int j = 0; j < input.height; j++) {
                
                int sum = 0;

                for(int[] d : deltas) {
                    int x = i + d[0];
                    int y = j + d[1];

                    if(x < 0 || x >= input.width || y < 0 || y >= input.height){
                        continue;
                    }
                    sum += (red(input.pixels[x + y * input.width]) > 0)? 1 : 0;
                }

                img.pixels[i + j * img.width] = sum > 1? color(255, 255, 255) : color(0, 0, 0);
            }
        }

        img.updatePixels();
        return img;
    }

    private int applyKernel(PImage img, int x, int y, double[][] matrix, int matrixsize)
    {
        float rtotal = 0.0f;
        float gtotal = 0.0f;
        float btotal = 0.0f;
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

    public PImage convolve(PImage input, double[][] kernel)
    {
        if(kernel[0].length != kernel.length)
            System.out.println("Error");        


        PImage img = createImage(input.width, input.height, RGB);
    
        input.loadPixels();    
        img.loadPixels();

        for (int x = 1; x < input.width - 1; x++)
            for(int y = 1; y < input.height - 1; y++)
                img.pixels[x + y * img.width] = applyKernel(input, x, y, kernel, kernel.length);

        img.updatePixels();
        return img;
    }

    
    public PImage sobel(PImage input, double[][] kernel)
    {
        if(kernel[0].length != kernel.length)
            System.out.println("Error");        


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

        img.updatePixels();
        return img;
    }

    public void keyPressed()
    {
        switch(key)
        {
            case 'q':
                System.out.println("|===== Camera Data =====|");
                System.out.println("Width: " + video.width);
                System.out.println("Height: " + video.height);
                break;
            case 'w':
                threshold += 1;
                System.out.println(threshold);
                break;
            case 's':
                threshold -= 1;
                System.out.println(threshold);
                break;
        }
    }


}